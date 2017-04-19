package org.zalando.apidiscovery.crawler.storage;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;
import org.zalando.stups.clients.kio.ApplicationBase;

import static org.zalando.apidiscovery.crawler.Utils.extractApiDefinitionUrl;

public class LegacyApiDiscoveryStorageGateway {

    private static final Logger LOG = LoggerFactory.getLogger(LegacyApiDiscoveryStorageGateway.class);

    private final RestOperations restOperations;
    private final String baseUrl;

    public LegacyApiDiscoveryStorageGateway(RestOperations restOperations, String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    public void createOrUpdateApiDefinition(JsonNode schemaDiscoveryInformation, JsonNode apiDefinitionInformation, ApplicationBase app) {
        Assert.hasText(app.getId(), "applicationId must not be blank");

        LegacyApiDefinition apiDefinition = LegacyApiDefinition.UNSUCCESSFUL;
        try {
            final String serviceUrl = app.getServiceUrl().endsWith("/") ? app.getServiceUrl() : app.getServiceUrl() + "/";
            apiDefinition = constructLegacyApiDefinition(schemaDiscoveryInformation, apiDefinitionInformation, serviceUrl);
        } catch (Exception e) {
            LOG.info("Could not construct legacy api definition request for {}: {}", app.getId(), e);
        }

        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("applicationId", app.getId());

        restOperations.put(baseUrl + "/apps/{applicationId}", apiDefinition, uriVariables);
    }

    protected static LegacyApiDefinition constructLegacyApiDefinition(JsonNode schemaDiscovery, JsonNode apiDefinition, String serviceUrl) throws Exception {
        return LegacyApiDefinition.builder()
                .status("SUCCESS")
                .type(schemaDiscovery.get("schema_type").asText(""))
                .name(apiDefinition.get("info").get("title").asText())
                .version(apiDefinition.get("info").get("version").asText())
                .serviceUrl(serviceUrl)
                .url(extractApiDefinitionUrl(schemaDiscovery))
                .ui(schemaDiscovery.has("ui_url") ? schemaDiscovery.get("ui_url").asText() : null)
                .definition(apiDefinition.toString())
                .build();
    }
}
