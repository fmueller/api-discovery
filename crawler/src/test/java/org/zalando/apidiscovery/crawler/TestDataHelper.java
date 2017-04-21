package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.zalando.apidiscovery.crawler.storage.ApiDefinition;
import org.zalando.apidiscovery.crawler.storage.LegacyApiDefinition;
import org.zalando.stups.clients.kio.ApplicationBase;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class TestDataHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static LegacyApiDefinition META_API_LEGACY_DEFINITION = LegacyApiDefinition.builder()
        .name("meta-api")
        .definition("{\"info\":{\"title\":\"meta-api\",\"version\":\"1.0\"}}")
        .url("swagger.json")
        .serviceUrl("https://meta.api/")
        .status("SUCCESS")
        .type("swagger-2.0")
        .ui("/ui")
        .version("1.0")
        .build();

    public static ApiDefinition META_API_DEFINITION = ApiDefinition.builder()
        .apiName("meta-api")
        .appName("meta-api-service")
        .definition("{\"info\":{\"title\":\"meta-api\",\"version\":\"1.0\"}}")
        .url("swagger.json")
        .serviceUrl("https://meta.api/")
        .status("SUCCESSFUL")
        .type("swagger-2.0")
        .ui("/ui")
        .version("1.0")
        .build();

    public static ApplicationBase metaApiApplication() {
        ApplicationBase application = new ApplicationBase();
        application.setServiceUrl("https://meta.api");
        application.setId("meta-api-service");
        return application;
    }

    public static JsonNode readJson(Resource resource) throws IOException {
        String content = Files.lines(resource.getFile().toPath()).collect(Collectors.joining());
        return objectMapper.readTree(content);
    }
}
