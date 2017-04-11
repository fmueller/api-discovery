package org.zalando.apidiscovery.storage.utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class SwaggerDefinitionHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonNode definition;

    public SwaggerDefinitionHelper(final String definition) throws IOException {
        this.definition = objectMapper.readTree(definition);
    }

    public String getName() throws IOException {
        final String rawName = definition.get("info").get("title").asText();
        return rawName.trim().toLowerCase().replace(" ", "-");
    }

    public String getVersion() throws IOException {
        return definition.get("info").get("version").asText();
    }
}
