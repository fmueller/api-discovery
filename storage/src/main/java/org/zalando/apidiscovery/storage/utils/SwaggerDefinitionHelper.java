package org.zalando.apidiscovery.storage.utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SwaggerDefinitionHelper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String nameOf(final String definition) throws IOException {
        final String rawName = parse(definition).get("info").get("title").asText();
        return rawName.trim().toLowerCase().replace(" ", "-");
    }

    public String versionOf(final String definition) throws IOException {
        return parse(definition).get("info").get("version").asText();
    }

    private JsonNode parse(final String definition) throws IOException {
        return objectMapper.readTree(definition);
    }
}
