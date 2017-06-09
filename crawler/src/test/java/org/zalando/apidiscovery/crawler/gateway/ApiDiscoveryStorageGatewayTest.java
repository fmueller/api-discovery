package org.zalando.apidiscovery.crawler.gateway;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;
import org.zalando.apidiscovery.crawler.CrawledApiDefinition;
import org.zalando.apidiscovery.crawler.KioApplication;
import org.zalando.apidiscovery.crawler.SchemaDiscovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.zalando.apidiscovery.crawler.TestDataHelper.META_API_DEFINITION;
import static org.zalando.apidiscovery.crawler.TestDataHelper.metaApiApplication;
import static org.zalando.apidiscovery.crawler.TestDataHelper.parseResource;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApiDiscoveryStorageGatewayTest {

    @Value("classpath:meta_api_schema_discovery_json_schema_url.json")
    private Resource metaApiSchemaDiscoveryJson;

    @Value("classpath:meta_api_definition.json")
    private Resource metaApiDefinitionJson;

    @Mock
    private RestOperations restOperations;

    @Mock
    private CrawledApiDefinition crawledApiDefinition;

    @Mock
    private KioApplication kioApplication;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldBeAbleToMapSchemaDiscoveryAndDefinitionToApiDefinition() throws Exception {
        ApiDefinition apiDefinition = ApiDiscoveryStorageGateway.constructApiDefinition(
            new SchemaDiscovery(parseResource(metaApiSchemaDiscoveryJson)),
            new CrawledApiDefinition(parseResource(metaApiDefinitionJson)),
            new KioApplication(metaApiApplication()));

        assertThat(apiDefinition).isEqualTo(META_API_DEFINITION);
    }

    @Test
    public void shouldPushSuccessfulApiDefinition() throws Exception {
        ApiDiscoveryStorageGateway storageGateway = new ApiDiscoveryStorageGateway(restOperations, "localhost");

        storageGateway.pushApiDefinition(
            new SchemaDiscovery(parseResource(metaApiSchemaDiscoveryJson)),
            new CrawledApiDefinition(parseResource(metaApiDefinitionJson)),
            new KioApplication(metaApiApplication()));

        verify(restOperations).postForLocation(anyString(), anyObject());
    }

    @Test
    public void shouldNotPushApiDefinitionWithoutApiName() throws Exception {
        ApiDiscoveryStorageGateway storageGateway = new ApiDiscoveryStorageGateway(restOperations, "localhost");

        when(crawledApiDefinition.getName()).thenReturn("  ");

        storageGateway.pushApiDefinition(
            new SchemaDiscovery(parseResource(metaApiSchemaDiscoveryJson)),
            crawledApiDefinition,
            new KioApplication(metaApiApplication()));

        verify(restOperations, never()).postForLocation(anyString(), anyObject());
    }

    @Test
    public void shouldNotPushApiDefinitionWithoutApplicationName() throws Exception {
        ApiDiscoveryStorageGateway storageGateway = new ApiDiscoveryStorageGateway(restOperations, "localhost");

        when(kioApplication.getName()).thenReturn("  ");

        storageGateway.pushApiDefinition(
            new SchemaDiscovery(parseResource(metaApiSchemaDiscoveryJson)),
            new CrawledApiDefinition(parseResource(metaApiDefinitionJson)),
            kioApplication);

        verify(restOperations, never()).postForLocation(anyString(), anyObject());
    }
}
