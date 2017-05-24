package org.zalando.apidiscovery.crawler.gateway;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.web.client.RestOperations;
import org.zalando.apidiscovery.crawler.CrawledApiDefinition;
import org.zalando.apidiscovery.crawler.KioApplication;
import org.zalando.apidiscovery.crawler.SchemaDiscovery;

import static org.zalando.apidiscovery.crawler.gateway.ApiDefinition.STATUS_SUCCESSFUL;
import static org.zalando.apidiscovery.crawler.gateway.ApiDefinition.STATUS_UNSUCCESSFUL;

public class ApiDiscoveryStorageGateway {

    private final RestOperations restOperations;
    private final String baseUrl;

    public ApiDiscoveryStorageGateway(RestOperations restOperations, String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    public void pushApiDefinition(SchemaDiscovery schemaDiscovery, CrawledApiDefinition crawledApiDefinition, KioApplication app) {
        final ApiDefinition apiDefinition = constructApiDefinition(schemaDiscovery, crawledApiDefinition, app);

        restOperations.postForLocation(baseUrl + "/api-definitions", apiDefinition);
    }

    @VisibleForTesting
    protected static ApiDefinition constructApiDefinition(SchemaDiscovery schemaDiscovery, CrawledApiDefinition apiDefinition, KioApplication app) {
        if (schemaDiscovery == null || apiDefinition == null) {
            return ApiDefinition.builder()
                .status(STATUS_UNSUCCESSFUL)
                .appName(app.getName())
                .build();
        }

        return ApiDefinition.builder()
            .status(STATUS_SUCCESSFUL)
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
