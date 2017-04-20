package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.zalando.apidiscovery.crawler.storage.ApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.storage.LegacyApiDiscoveryStorageGateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.zalando.apidiscovery.crawler.TestData.metaApiApplication;
import static org.zalando.apidiscovery.crawler.TestData.metaApiDefinition;
import static org.zalando.apidiscovery.crawler.TestData.metaApiSchemaDiscovery;

@RunWith(MockitoJUnitRunner.class)
public class ApiDefinitionCrawlJobTest {

    @Mock
    private LegacyApiDiscoveryStorageGateway legacyStorageClient;

    @Mock
    private ApiDiscoveryStorageGateway storageClient;

    @Mock
    private RestTemplate schemaClient;

    @Test
    public void shouldBeAbleToProcessUnsuccessfulCrawling() throws Exception {
        ApiDefinitionCrawlJob job = new ApiDefinitionCrawlJob(legacyStorageClient, storageClient, schemaClient, metaApiApplication());

        assertThat(job.call()).isNull();

        verify(legacyStorageClient).createOrUpdateApiDefinition(eq(null), eq(null), anyObject());
        verify(storageClient).pushApiDefinition(eq(null), eq(null), anyObject());
    }

    @Test
    public void shouldBeAbleToProcessSuccessfulCrawling() throws Exception {
        ResponseEntity<JsonNode> schema = new ResponseEntity<>(metaApiSchemaDiscovery(), HttpStatus.OK);
        ResponseEntity<JsonNode> api = new ResponseEntity<>(metaApiDefinition(), HttpStatus.OK);

        doReturn(schema).when(schemaClient).exchange(eq("https://meta.api/.well-known/schema-discovery"), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class));
        doReturn(api).when(schemaClient).exchange(eq("https://meta.api/swagger.json"), eq(HttpMethod.GET), anyObject(), eq(JsonNode.class));

        ApiDefinitionCrawlJob job = new ApiDefinitionCrawlJob(legacyStorageClient, storageClient, schemaClient, metaApiApplication());

        assertThat(job.call()).isNull();

        verify(legacyStorageClient).createOrUpdateApiDefinition(eq(metaApiSchemaDiscovery()), eq(metaApiDefinition()), anyObject());
        verify(storageClient).pushApiDefinition(eq(metaApiSchemaDiscovery()), eq(metaApiDefinition()), anyObject());
    }

}
