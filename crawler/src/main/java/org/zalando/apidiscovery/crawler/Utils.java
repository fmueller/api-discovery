package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;

public class Utils {
    public static String extractApiDefinitionUrl(JsonNode schemaDiscovery) {
        String apiDefinitionUrl = schemaDiscovery.get("schema_url").asText();
        if (apiDefinitionUrl.startsWith("/")) {
            apiDefinitionUrl = apiDefinitionUrl.substring(1);
        }
        return apiDefinitionUrl;
    }
}
