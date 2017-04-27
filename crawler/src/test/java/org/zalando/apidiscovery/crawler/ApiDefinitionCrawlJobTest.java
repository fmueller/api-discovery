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
import static org.zalando.apidiscovery.crawler.TestDataHelper.metaApiKioApplication;
import static org.zalando.apidiscovery.crawler.TestDataHelper.metaApiApplication;
import static org.zalando.apidiscovery.crawler.TestDataHelper.parseResource;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApiDefinitionCrawlJobTest {

    @Mock
    private LegacyApiDiscoveryStorageGateway legacyStorageGateway;

    @Mock
    private ApiDiscoveryStorageGateway storageGateway;

    @Mock
    private WellKnownSchemaGateway schemaGateway;

    @Value("classpath:meta_api_schema_discovery_json_schema_url.json")
    private Resource metaApiSchemaDiscoveryJson;

    @Value("classpath:meta_api_definition.json")
    private Resource metaApiDefinitionJson;

    private ApplicationBase metaApiApplication = metaApiApplication();
    private KioApplication metaApiKioApplication = metaApiKioApplication();

    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldBeAbleToProcessUnsuccessfulCrawling() throws Exception {
        ApiDefinitionCrawlJob job = new ApiDefinitionCrawlJob(legacyStorageGateway, storageGateway, schemaGateway, metaApiApplication);

        assertThat(job.call()).isEqualTo(CrawlResult.builder().successful(false).build());

        verify(legacyStorageGateway).createOrUpdateApiDefinition(eq(null), eq(null), eq(metaApiKioApplication));
        verify(storageGateway).pushApiDefinition(eq(null), eq(null), eq(metaApiKioApplication));
    }

    @Test
    public void shouldBeAbleToProcessSuccessfulCrawling() throws Exception {
        JsonNode metaApiSchemaDiscoveryJson = parseResource(this.metaApiSchemaDiscoveryJson);
        JsonNode metaApiDefinitionJson = parseResource(this.metaApiDefinitionJson);
        SchemaDiscovery schemaDiscovery = new SchemaDiscovery(metaApiSchemaDiscoveryJson);
        CrawledApiDefinition apiDefinition = new CrawledApiDefinition(metaApiDefinitionJson);

        doReturn(metaApiSchemaDiscoveryJson).when(schemaGateway).retrieveSchemaDiscovery(eq(metaApiKioApplication));
        doReturn(metaApiDefinitionJson).when(schemaGateway).retrieveApiDefinition(eq(metaApiKioApplication), any(SchemaDiscovery.class));

        ApiDefinitionCrawlJob job = new ApiDefinitionCrawlJob(legacyStorageGateway, storageGateway, schemaGateway, metaApiApplication);

        assertThat(job.call()).isEqualTo(CrawlResult.builder().successful(true).build());

        verify(legacyStorageGateway).createOrUpdateApiDefinition(eq(schemaDiscovery), eq(apiDefinition), eq(metaApiKioApplication));
        verify(storageGateway).pushApiDefinition(eq(schemaDiscovery), eq(apiDefinition), eq(metaApiKioApplication));
    }

}
