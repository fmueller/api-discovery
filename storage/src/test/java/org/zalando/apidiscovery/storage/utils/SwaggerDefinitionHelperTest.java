package org.zalando.apidiscovery.storage.utils;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.TestDataHelper.instagramApiDefinition;

public class SwaggerDefinitionHelperTest {

    private String instagramDefinition;
    private SwaggerDefinitionHelper swagger;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        swagger = new SwaggerDefinitionHelper();
        instagramDefinition = instagramApiDefinition();
    }

    @Test
    public void shouldExtractTheVersionTest() throws Exception {
        assertThat(swagger.versionOf(instagramDefinition)).isEqualTo("v1");
    }

    @Test
    public void shouldExtractAndManipulateTheNameTest() throws Exception {
        // the actual value of the api title is 'Instagram API '
        assertThat(swagger.nameOf(instagramDefinition)).isEqualTo("instagram-api");
    }
}
