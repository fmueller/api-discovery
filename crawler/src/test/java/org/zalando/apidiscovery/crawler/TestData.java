package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.zalando.apidiscovery.crawler.storage.ApiDefinition;
import org.zalando.apidiscovery.crawler.storage.LegacyApiDefinition;
import org.zalando.stups.clients.kio.ApplicationBase;

public class TestData {

    private static JsonNodeFactory factory = JsonNodeFactory.instance;

    public static LegacyApiDefinition META_API_LEGACY_DEFINITION = LegacyApiDefinition.builder()
            .name("meta-api")
            .definition("{\"info\":{\"version\":\"1.0\",\"title\":\"meta-api\"}}")
            .url("swagger.json")
            .serviceUrl("https://meta.api/")
            .status("SUCCESS")
            .type("swagger-2.0")
            .ui("/ui")
            .version("1.0")
            .build();

    public static  ApiDefinition META_API_DEFINITION = ApiDefinition.builder()
            .apiName("meta-api")
            .appName("meta-api-service")
            .definition("{\"info\":{\"version\":\"1.0\",\"title\":\"meta-api\"}}")
            .url("swagger.json")
            .serviceUrl("https://meta.api/")
            .status("SUCCESSFUL")
            .type("swagger-2.0")
            .ui("/ui")
            .version("1.0")
            .build();

    public static JsonNode metaApiSchemaDiscovery() {
        ObjectNode schemaDiscovery = new ObjectNode(factory);
        schemaDiscovery.put("ui_url", "/ui");
        schemaDiscovery.put("schema_url", "swagger.json");
        schemaDiscovery.put("schema_type", "swagger-2.0");
        return schemaDiscovery;
    }

    public static JsonNode metaApiDefinition() {
        ObjectNode apiDefinition = new ObjectNode(factory);
        ObjectNode info = new ObjectNode(factory);
        info.put("version", "1.0");
        info.put("title", "meta-api");
        apiDefinition.set("info", info);
        return apiDefinition;
    }

    public static ApplicationBase metaApiApplication() {
        ApplicationBase application = new ApplicationBase();
        application.setServiceUrl("https://meta.api");
        application.setId("meta-api-service");
        return application;
    }
}
