package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.zalando.apidiscovery.crawler.gateway.ApiDefinition;

@EqualsAndHashCode
@AllArgsConstructor
public class CrawledApiDefinition {

    private JsonNode apiDefinitionJson;

    public String getName() {
        return apiDefinitionJson.get("info").get("title").asText(ApiDefinition.UNDEFINED_TITLE);
    }

    public String getVersion() {
        return apiDefinitionJson.get("info").get("version").asText(ApiDefinition.UNDEFINED_VERSION);
    }

    public String getDefinition() {
        return apiDefinitionJson.toString();
    }

}
