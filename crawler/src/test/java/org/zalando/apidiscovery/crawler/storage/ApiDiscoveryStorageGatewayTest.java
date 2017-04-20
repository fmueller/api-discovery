package org.zalando.apidiscovery.crawler.storage;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.crawler.TestData.META_API_DEFINITION;
import static org.zalando.apidiscovery.crawler.TestData.metaApiApplication;
import static org.zalando.apidiscovery.crawler.TestData.metaApiDefinition;
import static org.zalando.apidiscovery.crawler.TestData.metaApiSchemaDiscovery;

public class ApiDiscoveryStorageGatewayTest {

    @Test
    public void shouldBeAbleToMapSchemaDiscoveryAndDefinitionToApiDefinition() throws Exception {
        ApiDefinition apiDefinition = ApiDiscoveryStorageGateway.constructApiDefinition(metaApiSchemaDiscovery(), metaApiDefinition(),
                metaApiApplication());

        assertThat(apiDefinition).isEqualTo(META_API_DEFINITION);
    }
}
