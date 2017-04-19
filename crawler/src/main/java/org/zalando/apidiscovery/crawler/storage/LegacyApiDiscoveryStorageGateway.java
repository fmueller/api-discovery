package org.zalando.apidiscovery.crawler.storage;

import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

import java.util.HashMap;
import java.util.Map;

public class LegacyApiDiscoveryStorageGateway {

    private final RestOperations restOperations;
    private final String baseUrl;

    public LegacyApiDiscoveryStorageGateway(RestOperations restOperations, String baseUrl) {
        this.restOperations = restOperations;
        this.baseUrl = baseUrl;
    }

    public void createOrUpdateApiDefinition(LegacyApiDefinition request, String applicationId) {
        Assert.hasText(applicationId, "applicationId must not be blank");

        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("applicationId", applicationId);
        restOperations.put(baseUrl + "/apps/{applicationId}", request, uriVariables);
    }

}
