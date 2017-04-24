package org.zalando.apidiscovery.crawler.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.zalando.stups.clients.kio.ApplicationBase;

import java.io.IOException;
import java.net.UnknownHostException;

@Slf4j
public class WellKnownSchemaGateway {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public WellKnownSchemaGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JsonNode retrieveSchemaDiscovery(ApplicationBase app) {
        final String serviceUrl = extractServiceUrl(app);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "*/*");
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                serviceUrl + ".well-known/schema-discovery", HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else if (responseEntity.getStatusCode().value() == 404) {
                log.info("Service {} does not implement api discovery", app.getId());
            } else {
                log.info("Error while loading api discovery of service {}: {}", app.getId(), responseEntity.getStatusCode());
            }
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof UnknownHostException) {
                log.info("Host for service {} is not reachable: {}", app.getId(), serviceUrl);
            } else {
                log.info("Service {} is not reachable: {}", app.getId(), e.getMessage());
            }
        } catch (Exception e) {
            log.info("Could not load api discovery info for service {}: {}", app.getId(), e.getMessage());
        }
        return null;
    }

    public JsonNode retrieveApiDefinition(ApplicationBase app, JsonNode schemaDiscovery) throws Exception {
        final String url = extractServiceUrl(app) + extractApiDefinitionUrl(schemaDiscovery);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "*/*");
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                log.info("Try to load api definition as json for service {}", app.getId());
                return tryRetrieveApiDefinitionAsYaml(url, app.getId());
            }
        } catch (Exception e) {
            return tryRetrieveApiDefinitionAsYaml(url, app.getId());
        }
    }

    @VisibleForTesting
    protected static String extractApiDefinitionUrl(JsonNode schemaDiscovery) {
        String apiDefinitionUrl = schemaDiscovery.get("schema_url").asText();
        if (apiDefinitionUrl.startsWith("/")) {
            apiDefinitionUrl = apiDefinitionUrl.substring(1);
        }
        return apiDefinitionUrl;
    }

    @VisibleForTesting
    protected static String extractServiceUrl(ApplicationBase app) {
        return app.getServiceUrl().endsWith("/") ? app.getServiceUrl() : app.getServiceUrl() + "/";
    }

    @VisibleForTesting
    protected JsonNode tryRetrieveApiDefinitionAsYaml(String url, String appId) throws IOException {
        log.info("Try to load api definition as yaml for service {}", appId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "*/*");
        ResponseEntity<String> yamlApiDefinition = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity(headers), String.class);

        if (!yamlApiDefinition.getStatusCode().is2xxSuccessful()) {
            log.info("Could not load yaml api definition");
            return null;
        }
        return objectMapper.readValue(yamlApiDefinition.getBody(), JsonNode.class);
    }
}
