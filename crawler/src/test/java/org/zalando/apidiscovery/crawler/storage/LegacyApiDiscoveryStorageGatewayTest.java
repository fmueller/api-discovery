package org.zalando.apidiscovery.crawler.storage;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.crawler.TestData.META_API_LEGACY_DEFINITION;
import static org.zalando.apidiscovery.crawler.TestData.metaApiDefinition;
import static org.zalando.apidiscovery.crawler.TestData.metaApiSchemaDiscovery;

public class LegacyApiDiscoveryStorageGatewayTest {

    @Test
    public void shouldBeAbleToMapSchemaDiscoveryAndDefinitionToLegacyApiDefinition() throws Exception {
        LegacyApiDefinition legacyApiDefinition = LegacyApiDiscoveryStorageGateway.constructLegacyApiDefinition(metaApiSchemaDiscovery(),
                metaApiDefinition(), "https://meta.api/");

        assertThat(legacyApiDefinition).isEqualTo(META_API_LEGACY_DEFINITION);
    }
}
