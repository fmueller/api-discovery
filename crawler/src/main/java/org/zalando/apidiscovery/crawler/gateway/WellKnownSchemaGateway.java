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
import org.zalando.apidiscovery.crawler.KioApplication;
import org.zalando.apidiscovery.crawler.SchemaDiscovery;

import java.io.IOException;
import java.net.UnknownHostException;

@Slf4j
public class WellKnownSchemaGateway {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    public WellKnownSchemaGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JsonNode retrieveSchemaDiscovery(KioApplication app) {
        final String serviceUrl = app.getServiceUrl();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "*/*");
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                serviceUrl + ".well-known/schema-discovery", HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else if (responseEntity.getStatusCode().value() == 404) {
                log.info("Service {} does not implement api discovery", app.getName());
            } else {
                log.info("Error while loading api discovery of service {}: {}", app.getName(), responseEntity.getStatusCode());
            }
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof UnknownHostException) {
                log.info("Host for service {} is not reachable: {}", app.getName(), serviceUrl);
            } else {
                log.info("Service {} is not reachable: {}", app.getName(), e.getMessage());
            }
        } catch (Exception e) {
            log.info("Could not load api discovery info for service {}: {}", app.getName(), e.getMessage());
        }
        return null;
    }

    public JsonNode retrieveApiDefinition(KioApplication app, SchemaDiscovery schemaDiscovery) throws Exception {
        final String url = app.getServiceUrl() + schemaDiscovery.getApiDefinitionUrl();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "*/*");
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                log.info("Try to load api definition as json for service {}", app.getName());
                return tryRetrieveApiDefinitionAsYaml(url, app.getName());
            }
        } catch (Exception e) {
            return tryRetrieveApiDefinitionAsYaml(url, app.getName());
        }
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
