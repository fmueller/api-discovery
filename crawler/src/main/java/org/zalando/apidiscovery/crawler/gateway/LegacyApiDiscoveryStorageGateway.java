package org.zalando.apidiscovery.crawler.gateway;

import java.util.HashMap;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;
import org.zalando.apidiscovery.crawler.CrawledApiDefinition;
import org.zalando.apidiscovery.crawler.KioApplication;
import org.zalando.apidiscovery.crawler.SchemaDiscovery;

public class LegacyApiDiscoveryStorageGateway {

    private final RestOperations restOperations;
    private final String baseUrl;

    public LegacyApiDiscoveryStorageGateway(RestOperations restOperations, String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    public void createOrUpdateApiDefinition(SchemaDiscovery schemaDiscovery, CrawledApiDefinition crawledApiDefinition,
                                            KioApplication app) {
        Assert.hasText(app.getName(), "application name must not be blank");

        LegacyApiDefinition apiDefinition;
        if (schemaDiscovery == null || crawledApiDefinition == null) {
            apiDefinition = LegacyApiDefinition.UNSUCCESSFUL;
        } else {
            apiDefinition = constructLegacyApiDefinition(schemaDiscovery, crawledApiDefinition, app);
        }

        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("applicationId", app.getName());

        restOperations.put(baseUrl + "/apps/{applicationId}", apiDefinition, uriVariables);
    }

    @VisibleForTesting
    protected static LegacyApiDefinition constructLegacyApiDefinition(SchemaDiscovery schemaDiscovery,
                                                                      CrawledApiDefinition apiDefinition,
                                                                      KioApplication app) {
        return LegacyApiDefinition.builder()
                .status(LegacyApiDefinition.STATUS_SUCCESS)
                .type(schemaDiscovery.getSchemaType())
                .name(apiDefinition.getName())
                .version(apiDefinition.getVersion())
                .serviceUrl(app.getServiceUrl())
                .url(schemaDiscovery.getApiDefinitionUrl())
                .ui(schemaDiscovery.getUiUrl())
                .definition(apiDefinition.getDefinition())
                .build();
    }
}
