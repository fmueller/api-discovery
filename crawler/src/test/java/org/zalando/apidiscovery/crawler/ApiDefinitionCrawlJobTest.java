package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.zalando.apidiscovery.crawler.storage.ApiDefinition;
import org.zalando.apidiscovery.crawler.storage.LegacyApiDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiDefinitionCrawlJobTest {

    private JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test
    public void shouldBeAbleToConstructApiDefinition() throws Exception {
        ApiDefinition apiDefinition = ApiDefinitionCrawlJob.constructApiDefinition(schemaDiscovery(), apiDefinition(),
                "swagger-2.0", "meta-api-service", "/api", "https://meta.api");

        assertThat(apiDefinition.getApiName()).isEqualTo("meta-api");
        assertThat(apiDefinition.getAppName()).isEqualTo("meta-api-service");
        assertThat(apiDefinition.getDefinition()).isEqualTo("{\"info\":{\"version\":\"1.0\",\"title\":\"meta-api\"}}");
        assertThat(apiDefinition.getSchemaUrl()).isEqualTo("/api");
        assertThat(apiDefinition.getServiceUrl()).isEqualTo("https://meta.api");
        assertThat(apiDefinition.getStatus()).isEqualTo("SUCCESS");
        assertThat(apiDefinition.getType()).isEqualTo("swagger-2.0");
        assertThat(apiDefinition.getUiLink()).isEqualTo("/ui");
        assertThat(apiDefinition.getVersion()).isEqualTo("1.0");
    }

    @Test
    public void shouldBeAbleToConstructLegacyApiDefinition() throws Exception {
        LegacyApiDefinition legacyApiDefinition = ApiDefinitionCrawlJob.constructLegacyApiDefinition(schemaDiscovery(), apiDefinition(),
                "swagger-2.0", "/api", "https://meta.api");

        assertThat(legacyApiDefinition.getName()).isEqualTo("meta-api");
        assertThat(legacyApiDefinition.getDefinition()).isEqualTo("{\"info\":{\"version\":\"1.0\",\"title\":\"meta-api\"}}");
        assertThat(legacyApiDefinition.getSchemaUrl()).isEqualTo("/api");
        assertThat(legacyApiDefinition.getServiceUrl()).isEqualTo("https://meta.api");
        assertThat(legacyApiDefinition.getStatus()).isEqualTo("SUCCESS");
        assertThat(legacyApiDefinition.getType()).isEqualTo("swagger-2.0");
        assertThat(legacyApiDefinition.getUiLink()).isEqualTo("/ui");
        assertThat(legacyApiDefinition.getVersion()).isEqualTo("1.0");
    }

    private JsonNode schemaDiscovery() {
        ObjectNode schemaDiscovery = new ObjectNode(factory);
        schemaDiscovery.put("ui_url", "/ui");
        return schemaDiscovery;
    }

    private JsonNode apiDefinition() {
        ObjectNode apiDefinition = new ObjectNode(factory);
        ObjectNode info = new ObjectNode(factory);
        info.put("version", "1.0");
        info.put("title", "meta-api");
        apiDefinition.set("info", info);
        return apiDefinition;
    }
}
