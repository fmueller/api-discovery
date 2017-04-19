package org.zalando.apidiscovery.crawler;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.zalando.apidiscovery.crawler.storage.ApiDefinition;
import org.zalando.apidiscovery.crawler.storage.ApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.storage.LegacyApiDefinition;
import org.zalando.apidiscovery.crawler.storage.LegacyApiDiscoveryStorageGateway;
import org.zalando.stups.clients.kio.ApplicationBase;

class ApiDefinitionCrawlJob implements Callable<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDefinitionCrawlJob.class);

    private final LegacyApiDiscoveryStorageGateway legacyStorageClient;
    private final ApiDiscoveryStorageGateway storageClient;
    private final RestTemplate schemaClient;
    private final ApplicationBase app;

    ApiDefinitionCrawlJob(LegacyApiDiscoveryStorageGateway legacyStorageClient, ApiDiscoveryStorageGateway storageClient,
                          RestTemplate schemaClient, ApplicationBase app) {
        this.legacyStorageClient = legacyStorageClient;
        this.storageClient = storageClient;
        this.schemaClient = schemaClient;
        this.app = app;
    }

    @Override
    public Void call() throws Exception {
        LegacyApiDefinition legacyApiDefinition = LegacyApiDefinition.UNSUCCESSFUL;
        ApiDefinition apiDefinition = ApiDefinition.UNSUCCESSFUL;

        try {
            final String serviceUrl = app.getServiceUrl().endsWith("/") ? app.getServiceUrl() : app.getServiceUrl() + "/";
            final Optional<JsonNode> schemaDiscovery = retrieveSchemaDiscovery(serviceUrl);

            if (schemaDiscovery.isPresent()) {
                final JsonNode schemaDiscoveryInformation = schemaDiscovery.get();
                final JsonNode apiDefinitionInformation = retrieveApiDefinition(serviceUrl + apiDefinitionUrl(schemaDiscoveryInformation));

                legacyApiDefinition = constructLegacyApiDefinition(schemaDiscoveryInformation, apiDefinitionInformation, serviceUrl);
                apiDefinition = constructApiDefinition(schemaDiscoveryInformation, apiDefinitionInformation, app.getId(), serviceUrl);
                LOG.info("Successfully crawled api definition of {}", app.getId());
            } else {
                LOG.info("Api definition unavailable for {}", app.getId());
            }
        } catch (Exception e) {
            LOG.info("Could not crawl {}: {}", app.getId(), e.getMessage());
        }

        pushApiDefinitionsToLegacyAndNewEndpoint(apiDefinition, legacyApiDefinition, app.getId());
        return null;
    }

    private void pushApiDefinitionsToLegacyAndNewEndpoint(ApiDefinition apiDefinition, LegacyApiDefinition legacyApiDefinition, String appId) {
        try {
            legacyStorageClient.createOrUpdateApiDefinition(legacyApiDefinition, appId);
        } catch (Exception e) {
            LOG.warn("Could not send {} api definition to legacy endpoint: {}", app.getId(), e.getMessage());
        }

        try {
            storageClient.pushApiDefinition(apiDefinition);
        } catch (Exception e) {
            LOG.warn("Could not send api definition: {}", e.getMessage());
        }
    }

    protected static ApiDefinition constructApiDefinition(JsonNode schemaDiscovery, JsonNode apiDefinition,
                                                          String appName, String serviceUrl) throws Exception {
        return ApiDefinition.builder()
                .status("SUCCESSFUL")
                .type(schemaDiscovery.get("schema_type").asText(""))
                .apiName(apiDefinition.get("info").get("title").asText())
                .appName(appName)
                .version(apiDefinition.get("info").get("version").asText())
                .serviceUrl(serviceUrl)
                .url(apiDefinitionUrl(schemaDiscovery))
                .ui(schemaDiscovery.has("ui_url") ? schemaDiscovery.get("ui_url").asText() : null)
                .definition(apiDefinition.toString())
                .build();
    }

    private static String apiDefinitionUrl(JsonNode schemaDiscovery) {
        String apiDefinitionUrl = schemaDiscovery.get("schema_url").asText();
        if (apiDefinitionUrl.startsWith("/")) {
            apiDefinitionUrl = apiDefinitionUrl.substring(1);
        }
        return apiDefinitionUrl;
    }

    protected static LegacyApiDefinition constructLegacyApiDefinition(JsonNode schemaDiscovery, JsonNode apiDefinition, String serviceUrl) throws Exception {
        return LegacyApiDefinition.builder()
                .status("SUCCESS")
                .type(schemaDiscovery.get("schema_type").asText(""))
                .name(apiDefinition.get("info").get("title").asText())
                .version(apiDefinition.get("info").get("version").asText())
                .serviceUrl(serviceUrl)
                .url(apiDefinitionUrl(schemaDiscovery))
                .ui(schemaDiscovery.has("ui_url") ? schemaDiscovery.get("ui_url").asText() : null)
                .definition(apiDefinition.toString())
                .build();
    }

    private Optional<JsonNode> retrieveSchemaDiscovery(String serviceUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "*/*");
            ResponseEntity<JsonNode> responseEntity = schemaClient.exchange(
                    serviceUrl + ".well-known/schema-discovery", HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return Optional.of(responseEntity.getBody());
            } else if (responseEntity.getStatusCode().value() == 404) {
                LOG.info("Service {} does not implement api discovery", app.getId());
            } else {
                LOG.info("Error while loading api discovery of service {}: {}", app.getId(), responseEntity.getStatusCode());
            }
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof UnknownHostException) {
                LOG.info("Host for service {} is not reachable: {}", app.getId(), serviceUrl);
            } else {
                LOG.info("Service {} is not reachable: {}", app.getId(), e.getMessage());
            }
        } catch (Exception e) {
            LOG.info("Could not load api discovery info for service {}: {}", app.getId(), e.getMessage());
        }
        return Optional.empty();
    }

    private JsonNode retrieveApiDefinition(String url) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "*/*");
            ResponseEntity<JsonNode> responseEntity = schemaClient.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            return responseEntity.getStatusCode().is2xxSuccessful()
                    ? responseEntity.getBody()
                    : tryRetrieveApiDefinitionAsYaml(url);
        } catch (Exception e) {
            return tryRetrieveApiDefinitionAsYaml(url);
        }
    }

    private JsonNode tryRetrieveApiDefinitionAsYaml(String url) throws IOException {
        LOG.info("Try to load api definition as yaml for service {}", app.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "*/*");
        ResponseEntity<String> yamlApiDefinition = schemaClient.exchange(url, HttpMethod.GET, new HttpEntity(headers), String.class);

        if (!yamlApiDefinition.getStatusCode().is2xxSuccessful()) {
            throw new HttpClientErrorException(yamlApiDefinition.getStatusCode(), "Could not load yaml api definition");
        }
        return new ObjectMapper(new YAMLFactory()).readValue(yamlApiDefinition.getBody(), JsonNode.class);
    }
}
