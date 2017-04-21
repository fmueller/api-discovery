package org.zalando.apidiscovery.crawler.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.web.client.RestOperations;
import org.zalando.stups.clients.kio.ApplicationBase;

import static org.zalando.apidiscovery.crawler.WellKnownSchemaGateway.extractApiDefinitionUrl;

public class ApiDiscoveryStorageGateway {

    private final RestOperations restOperations;
    private final String baseUrl;

    public ApiDiscoveryStorageGateway(RestOperations restOperations, String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    public void pushApiDefinition(JsonNode schemaDiscoveryInformation, JsonNode apiDefinitionInformation, ApplicationBase app) {
        final ApiDefinition apiDefinition;

        if (schemaDiscoveryInformation == null || apiDefinitionInformation == null) {
            apiDefinition = ApiDefinition.UNSUCCESSFUL;
        } else {
            apiDefinition = constructApiDefinition(schemaDiscoveryInformation, apiDefinitionInformation, app);
        }

        restOperations.postForLocation(baseUrl + "/api-definitions", apiDefinition);
    }

    @VisibleForTesting
    protected static ApiDefinition constructApiDefinition(JsonNode schemaDiscovery, JsonNode apiDefinition, ApplicationBase app) {
        String serviceUrl = app.getServiceUrl().endsWith("/") ? app.getServiceUrl() : app.getServiceUrl() + "/";

        return ApiDefinition.builder()
            .status("SUCCESSFUL")
            .type(schemaDiscovery.get("schema_type").asText(""))
            .apiName(apiDefinition.get("info").get("title").asText())
            .appName(app.getId())
            .version(apiDefinition.get("info").get("version").asText())
            .serviceUrl(serviceUrl)
            .url(extractApiDefinitionUrl(schemaDiscovery))
            .ui(schemaDiscovery.has("ui_url") ? schemaDiscovery.get("ui_url").asText() : null)
            .definition(apiDefinition.toString())
            .build();
    }
}
