package org.zalando.apidiscovery.storage.api.domain.logic;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class SwaggerDefinitionHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode definition;

    public SwaggerDefinitionHelper(final String definition) throws SwaggerParseException {
        try {
            this.definition = objectMapper.readTree(definition);
        } catch (IOException | NullPointerException e) {
            throw new SwaggerParseException("could not parse swagger definition json", e);
        }
    }

    public String getName() throws SwaggerParseException {
        try {
            final String rawName = definition.get("info").get("title").asText();
            return rawName.trim().toLowerCase().replace(" ", "-");
        } catch (NullPointerException e) {
            throw new SwaggerParseException("could not extract the name of the api", e);
        }
    }

    public String getVersion() throws SwaggerParseException {
        try {
            return definition.get("info").get("version").asText();
        } catch (NullPointerException e) {
            throw new SwaggerParseException("could not extract the version of the api", e);
        }
    }
}
