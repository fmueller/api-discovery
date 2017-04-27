package org.zalando.apidiscovery.crawler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.crawler.TestDataHelper.parseResource;

@RunWith(SpringJUnit4ClassRunner.class)
public class SchemaDiscoveryTest {

    @Value("classpath:meta_api_schema_discovery_json_schema_url.json")
    private Resource metaApiSchemaDiscoveryJson;

    private SchemaDiscovery schemaDiscovery;

    @Before
    public void setUp() throws Exception {
        schemaDiscovery = new SchemaDiscovery(parseResource(metaApiSchemaDiscoveryJson));
    }

    @Test
    public void shouldExtractSchemaType() throws Exception {
        assertThat(schemaDiscovery.getSchemaType()).isEqualTo("swagger-2.0");
    }

    @Test
    public void shouldExtractApiDefinitionUrl() throws Exception {
        assertThat(schemaDiscovery.getApiDefinitionUrl()).isEqualTo("swagger.json");
    }

    @Test
    public void shouldExtractUiUrl() throws Exception {
        assertThat(schemaDiscovery.getUiUrl()).isEqualTo("/ui");
    }
}
