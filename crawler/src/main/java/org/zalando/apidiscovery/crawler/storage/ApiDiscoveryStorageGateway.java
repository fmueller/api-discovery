package org.zalando.apidiscovery.crawler.storage;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;
import org.zalando.stups.clients.kio.ApplicationBase;

import static org.zalando.apidiscovery.crawler.Utils.extractApiDefinitionUrl;

public class ApiDiscoveryStorageGateway {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDiscoveryStorageGateway.class);

    private final RestOperations restOperations;
    private final String baseUrl;

    public ApiDiscoveryStorageGateway(RestOperations restOperations, String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    public void pushApiDefinition(JsonNode schemaDiscoveryInformation, JsonNode apiDefinitionInformation, ApplicationBase app) {
        ApiDefinition apiDefinition = ApiDefinition.UNSUCCESSFUL;
        try {
            final String serviceUrl = app.getServiceUrl().endsWith("/") ? app.getServiceUrl() : app.getServiceUrl() + "/";
            apiDefinition = constructApiDefinition(schemaDiscoveryInformation, apiDefinitionInformation, app.getId(), serviceUrl);
        } catch (Exception e) {
            LOG.info("Could not construct api definition request for {}: {}", app.getId(), e);
        }
        
        restOperations.postForLocation(baseUrl + "/api-definitions", apiDefinition);
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
                .url(extractApiDefinitionUrl(schemaDiscovery))
                .ui(schemaDiscovery.has("ui_url") ? schemaDiscovery.get("ui_url").asText() : null)
                .definition(apiDefinition.toString())
                .build();
    }
}
