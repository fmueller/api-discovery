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
import org.zalando.apidiscovery.crawler.storage.ApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.storage.LegacyApiDiscoveryStorageGateway;
import org.zalando.stups.clients.kio.ApplicationBase;

import static org.zalando.apidiscovery.crawler.Utils.extractApiDefinitionUrl;

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
        JsonNode apiDefinitionInformation = null;
        JsonNode schemaDiscoveryInformation = null;
        try {
            final String serviceUrl = app.getServiceUrl().endsWith("/") ? app.getServiceUrl() : app.getServiceUrl() + "/";
            final Optional<JsonNode> schemaDiscovery = retrieveSchemaDiscovery(serviceUrl);

            if (schemaDiscovery.isPresent()) {
                schemaDiscoveryInformation = schemaDiscovery.get();
                apiDefinitionInformation = retrieveApiDefinition(serviceUrl + extractApiDefinitionUrl(schemaDiscoveryInformation));

                LOG.info("Successfully crawled api definition of {}", app.getId());
            } else {
                LOG.info("Api definition unavailable for {}", app.getId());
            }
        } catch (Exception e) {
            LOG.info("Could not crawl {}: {}", app.getId(), e.getMessage());
        }
        pushApiDefinitionsToLegacyAndNewEndpoint(schemaDiscoveryInformation, apiDefinitionInformation, app);
        return null;
    }

    private void pushApiDefinitionsToLegacyAndNewEndpoint(JsonNode schemaDiscoveryInformation, JsonNode apiDefinitionInformation, ApplicationBase app) {
        try {
            legacyStorageClient.createOrUpdateApiDefinition(schemaDiscoveryInformation, apiDefinitionInformation, app);
        } catch (Exception e) {
            LOG.info("Could not send {} api definition to legacy endpoint: {}", app.getId(), e.getMessage());
        }

        try {
            storageClient.pushApiDefinition(schemaDiscoveryInformation, apiDefinitionInformation, app);
        } catch (Exception e) {
            LOG.info("Could not send api definition: {}", e.getMessage());
        }
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

