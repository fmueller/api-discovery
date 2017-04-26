package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.zalando.apidiscovery.crawler.gateway.ApiDefinition;

@EqualsAndHashCode
@AllArgsConstructor
public class SchemaDiscovery {

    private JsonNode schemaDiscoveryJson;

    public String getSchemaType() {
        return schemaDiscoveryJson.get("schema_type").asText(ApiDefinition.UNDEFINED_SCHEMA_TYPE);
    }

    public String getApiDefinitionUrl() {
        String apiDefinitionUrl = schemaDiscoveryJson.get("schema_url").asText();
        if (apiDefinitionUrl.startsWith("/")) {
            apiDefinitionUrl = apiDefinitionUrl.substring(1);
        }
        return apiDefinitionUrl;
    }

    public String getUiUrl() {
        return schemaDiscoveryJson.has("ui_url") ? schemaDiscoveryJson.get("ui_url").asText() : null;
    }
}
