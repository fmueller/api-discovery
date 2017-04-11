package org.zalando.apidiscovery.storage.utils;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.TestDataHelper.instagramApiDefinition;

public class SwaggerDefinitionHelperTest {

    private SwaggerDefinitionHelper swagger;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        swagger = new SwaggerDefinitionHelper(instagramApiDefinition());
    }

    @Test
    public void shouldExtractTheVersionTest() throws Exception {
        assertThat(swagger.getVersion()).isEqualTo("v1");
    }

    @Test
    public void shouldExtractAndManipulateTheNameTest() throws Exception {
        // the actual value of the api title is 'Instagram API '
        assertThat(swagger.getName()).isEqualTo("instagram-api");
    }
}
