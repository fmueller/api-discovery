package org.zalando.apidiscovery.crawler.gateway;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.web.client.RestOperations;
import org.zalando.apidiscovery.crawler.CrawledApiDefinition;
import org.zalando.apidiscovery.crawler.KioApplication;
import org.zalando.apidiscovery.crawler.SchemaDiscovery;

public class ApiDiscoveryStorageGateway {

    private final RestOperations restOperations;
    private final String baseUrl;

    public ApiDiscoveryStorageGateway(RestOperations restOperations, String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    public void pushApiDefinition(SchemaDiscovery schemaDiscovery, CrawledApiDefinition crawledApiDefinition, KioApplication app) {
        final ApiDefinition apiDefinition;

        if (schemaDiscovery == null || crawledApiDefinition == null) {
            apiDefinition = ApiDefinition.UNSUCCESSFUL;
        } else {
            apiDefinition = constructApiDefinition(schemaDiscovery, crawledApiDefinition, app);
        }

        restOperations.postForLocation(baseUrl + "/api-definitions", apiDefinition);
    }

    @VisibleForTesting
    protected static ApiDefinition constructApiDefinition(SchemaDiscovery schemaDiscovery, CrawledApiDefinition apiDefinition, KioApplication app) {
        return ApiDefinition.builder()
            .status(ApiDefinition.STATUS_SUCCESSFUL)
            .type(schemaDiscovery.getSchemaType())
            .apiName(apiDefinition.getName())
            .appName(app.getName())
            .version(apiDefinition.getVersion())
            .serviceUrl(app.getServiceUrl())
            .url(schemaDiscovery.getApiDefinitionUrl())
            .ui(schemaDiscovery.getUiUrl())
            .definition(apiDefinition.getDefinition())
            .build();
    }
}
