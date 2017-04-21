package org.zalando.apidiscovery.crawler.gateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.crawler.TestDataHelper.META_API_LEGACY_DEFINITION;
import static org.zalando.apidiscovery.crawler.TestDataHelper.metaApiApplication;
import static org.zalando.apidiscovery.crawler.TestDataHelper.readJson;

@RunWith(SpringJUnit4ClassRunner.class)
public class LegacyApiDiscoveryStorageGatewayTest {

    @Value("classpath:meta_api_schema_discovery.json")
    private Resource metaApiSchemaDiscoveryResource;

    @Value("classpath:meta_api_definition.json")
    private Resource metaApiDefinitionResource;

    @Test
    public void shouldBeAbleToMapSchemaDiscoveryAndDefinitionToLegacyApiDefinition() throws Exception {
        LegacyApiDefinition legacyApiDefinition = LegacyApiDiscoveryStorageGateway.constructLegacyApiDefinition(
            readJson(metaApiSchemaDiscoveryResource),
            readJson(metaApiDefinitionResource),
            metaApiApplication());

        assertThat(legacyApiDefinition).isEqualTo(META_API_LEGACY_DEFINITION);
    }
}
