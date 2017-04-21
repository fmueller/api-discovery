package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.apidiscovery.crawler.gateway.ApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.LegacyApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.WellKnownSchemaGateway;
import org.zalando.stups.clients.kio.ApplicationBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.zalando.apidiscovery.crawler.TestDataHelper.metaApiApplication;
import static org.zalando.apidiscovery.crawler.TestDataHelper.readJson;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApiDefinitionCrawlJobTest {

    @Mock
    private LegacyApiDiscoveryStorageGateway legacyStorageGateway;

    @Mock
    private ApiDiscoveryStorageGateway storageGateway;

    @Mock
    private WellKnownSchemaGateway schemaGateway;

    @Value("classpath:meta_api_schema_discovery.json")
    private Resource metaApiSchemaDiscoveryResource;

    @Value("classpath:meta_api_definition.json")
    private Resource metaApiDefinitionResource;

    private ApplicationBase metaApiApplication = metaApiApplication();

    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldBeAbleToProcessUnsuccessfulCrawling() throws Exception {
        ApiDefinitionCrawlJob job = new ApiDefinitionCrawlJob(legacyStorageGateway, storageGateway, schemaGateway, metaApiApplication);

        assertThat(job.call()).isNull();

        verify(legacyStorageGateway).createOrUpdateApiDefinition(eq(null), eq(null), eq(metaApiApplication));
        verify(storageGateway).pushApiDefinition(eq(null), eq(null), eq(metaApiApplication));
    }

    @Test
    public void shouldBeAbleToProcessSuccessfulCrawling() throws Exception {
        JsonNode metaApiSchemaDiscovery = readJson(metaApiSchemaDiscoveryResource);
        JsonNode metaApiDefinition = readJson(metaApiDefinitionResource);

        doReturn(metaApiSchemaDiscovery).when(schemaGateway).retrieveSchemaDiscovery(eq(metaApiApplication));
        doReturn(metaApiDefinition).when(schemaGateway).retrieveApiDefinition(eq(metaApiApplication), any(JsonNode.class));

        ApiDefinitionCrawlJob job = new ApiDefinitionCrawlJob(legacyStorageGateway, storageGateway, schemaGateway, metaApiApplication);

        assertThat(job.call()).isNull();

        verify(legacyStorageGateway).createOrUpdateApiDefinition(eq(metaApiSchemaDiscovery), eq(metaApiDefinition), eq(metaApiApplication));
        verify(storageGateway).pushApiDefinition(eq(metaApiSchemaDiscovery), eq(metaApiDefinition), eq(metaApiApplication));
    }

}
