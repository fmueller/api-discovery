package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.zalando.apidiscovery.crawler.storage.ApiDefinition;
import org.zalando.apidiscovery.crawler.storage.ApiDiscoveryStorageClient;
import org.zalando.apidiscovery.crawler.storage.LegacyApiDefinition;
import org.zalando.apidiscovery.crawler.storage.LegacyApiDiscoveryStorageClient;
import org.zalando.stups.clients.kio.ApplicationBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ApiDefinitionCrawlJobTest {

    private JsonNodeFactory factory = JsonNodeFactory.instance;

    @Mock
    private LegacyApiDiscoveryStorageClient legacyStorageClient;

    @Mock
    private ApiDiscoveryStorageClient storageClient;

    @Mock
    private RestTemplate schemaClient;

    private LegacyApiDefinition expectedMetaApiLegacyDefinition = LegacyApiDefinition.builder()
            .name("meta-api")
            .definition("{\"info\":{\"version\":\"1.0\",\"title\":\"meta-api\"}}")
            .schemaUrl("swagger.json")
            .serviceUrl("https://meta.api/")
            .status("SUCCESS")
            .type("swagger-2.0")
            .uiLink("/ui")
            .version("1.0")
            .build();

    private ApiDefinition expectedMetaApiDefinition = ApiDefinition.builder()
            .apiName("meta-api")
            .appName("meta-api-service")
            .definition("{\"info\":{\"version\":\"1.0\",\"title\":\"meta-api\"}}")
            .schemaUrl("swagger.json")
            .serviceUrl("https://meta.api/")
            .status("SUCCESSFUL")
            .type("swagger-2.0")
            .uiLink("/ui")
            .version("1.0")
            .build();

    @Test
    public void shouldBeAbleToConstructApiDefinition() throws Exception {
        ApiDefinition apiDefinition = ApiDefinitionCrawlJob.constructApiDefinition(metaApiSchemaDiscovery(), metaApiApiDefinition(),
                "meta-api-service", "https://meta.api/");

        assertThat(apiDefinition).isEqualTo(expectedMetaApiDefinition);
    }

    @Test
    public void shouldBeAbleToConstructLegacyApiDefinition() throws Exception {
        LegacyApiDefinition legacyApiDefinition = ApiDefinitionCrawlJob.constructLegacyApiDefinition(metaApiSchemaDiscovery(),
                metaApiApiDefinition(), "https://meta.api/");

        assertThat(legacyApiDefinition).isEqualTo(expectedMetaApiLegacyDefinition);
    }

    @Test
    public void shouldBeAbleToProcessUnsuccessfulCrawling() throws Exception {
        ApiDefinitionCrawlJob job = new ApiDefinitionCrawlJob(legacyStorageClient, storageClient, schemaClient, metaApiApplication());

        assertThat(job.call()).isNull();

        verify(legacyStorageClient).createOrUpdateApiDefinition(eq(LegacyApiDefinition.UNSUCCESSFUL), eq("meta-api-service"));
        verify(storageClient).pushApiDefinition(eq(ApiDefinition.UNSUCCESSFUL));
    }

    @Test
    public void shouldBeAbleToProcessSuccessfulCrawling() throws Exception {
        ResponseEntity<JsonNode> schema = new ResponseEntity<>(metaApiSchemaDiscovery(), HttpStatus.OK);
        ResponseEntity<JsonNode> api = new ResponseEntity<>(metaApiApiDefinition(), HttpStatus.OK);

        doReturn(schema).when(schemaClient).exchange(eq("https://meta.api/.well-known/schema-discovery"), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class));
        doReturn(api).when(schemaClient).exchange(eq("https://meta.api/swagger.json"), eq(HttpMethod.GET), anyObject(), eq(JsonNode.class));

        ApiDefinitionCrawlJob job = new ApiDefinitionCrawlJob(legacyStorageClient, storageClient, schemaClient, metaApiApplication());

        assertThat(job.call()).isNull();

        verify(legacyStorageClient).createOrUpdateApiDefinition(eq(expectedMetaApiLegacyDefinition), eq("meta-api-service"));
        verify(storageClient).pushApiDefinition(eq(expectedMetaApiDefinition));
    }

    private JsonNode metaApiSchemaDiscovery() {
        ObjectNode schemaDiscovery = new ObjectNode(factory);
        schemaDiscovery.put("ui_url", "/ui");
        schemaDiscovery.put("schema_url", "swagger.json");
        schemaDiscovery.put("schema_type", "swagger-2.0");
        return schemaDiscovery;
    }

    private JsonNode metaApiApiDefinition() {
        ObjectNode apiDefinition = new ObjectNode(factory);
        ObjectNode info = new ObjectNode(factory);
        info.put("version", "1.0");
        info.put("title", "meta-api");
        apiDefinition.set("info", info);
        return apiDefinition;
    }

    private static ApplicationBase metaApiApplication() {
        ApplicationBase application = new ApplicationBase();
        application.setServiceUrl("https://meta.api");
        application.setId("meta-api-service");
        return application;
    }
}
