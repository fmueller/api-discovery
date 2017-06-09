package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.zalando.apidiscovery.crawler.gateway.ApiDefinition;

@EqualsAndHashCode
@AllArgsConstructor
public class CrawledApiDefinition {

    private JsonNode apiDefinitionJson;

    public String getName() {
        return apiDefinitionJson.has("info") && apiDefinitionJson.get("info").has("title")
            ? apiDefinitionJson.get("info").get("title").asText()
            : StringUtils.EMPTY;
    }

    public String getVersion() {
        return apiDefinitionJson.get("info").get("version").asText(ApiDefinition.UNDEFINED_VERSION);
    }

    public String getDefinition() {
        return apiDefinitionJson.toString();
    }

}
