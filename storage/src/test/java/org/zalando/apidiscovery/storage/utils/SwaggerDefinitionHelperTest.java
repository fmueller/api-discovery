package org.zalando.apidiscovery.storage.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.TestDataHelper.instagramApiDefinition;

public class SwaggerDefinitionHelperTest {

    @Test
    public void shouldExtractTheVersionTest() throws Exception {
        final SwaggerDefinitionHelper swagger = new SwaggerDefinitionHelper(instagramApiDefinition());

        assertThat(swagger.getVersion()).isEqualTo("v1");
    }

    @Test
    public void shouldExtractAndManipulateTheNameTest() throws Exception {
        final SwaggerDefinitionHelper swagger = new SwaggerDefinitionHelper(instagramApiDefinition());

        // the actual value of the api title is 'Instagram API '
        assertThat(swagger.getName()).isEqualTo("instagram-api");
    }

    @Test(expected = SwaggerParseException.class)
    public void shouldThrowExceptionIfVersionCannotBeParsedTest() throws Exception {
        final SwaggerDefinitionHelper swagger = new SwaggerDefinitionHelper("{\"info\": {\"title\": \"Some Api\"}}");

        swagger.getVersion();
    }

    @Test(expected = SwaggerParseException.class)
    public void shouldThrowExceptionIfNameCannotBeParsedTest() throws Exception {
        final SwaggerDefinitionHelper swagger = new SwaggerDefinitionHelper("{\"info\": {\"version\": \"1.0\"}}");

        swagger.getName();
    }

    @Test(expected = SwaggerParseException.class)
    public void shouldThrowExceptionIfDefinitionCannotBeParsedTest() throws Exception {
        new SwaggerDefinitionHelper("{");
    }
}
