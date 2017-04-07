package org.zalando.apidiscovery.crawler.storage;

import org.springframework.web.client.RestOperations;

public class ApiDiscoveryStorageClient {

    private final RestOperations restOperations;
    private final String baseUrl;

    public ApiDiscoveryStorageClient(RestOperations restOperations, String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    public void pushApiDefintion(ApiDefinition request) {
        restOperations.postForLocation(baseUrl + "/api-definitions", request);
    }
}
