package org.zalando.apidiscovery.crawler.storage;

import org.springframework.web.client.RestOperations;

public class ApiDiscoveryStorageGateway {

    private final RestOperations restOperations;
    private final String baseUrl;

    public ApiDiscoveryStorageGateway(RestOperations restOperations, String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    public void pushApiDefinition(ApiDefinition request) {
        restOperations.postForLocation(baseUrl + "/api-definitions", request);
    }
}
