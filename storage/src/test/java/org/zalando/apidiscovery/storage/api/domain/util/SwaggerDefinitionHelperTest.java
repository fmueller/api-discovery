package org.zalando.apidiscovery.storage.api.domain.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.apidiscovery.storage.api.domain.SwaggerParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.api.TestDataHelper.readResource;

@RunWith(SpringJUnit4ClassRunner.class)
public class SwaggerDefinitionHelperTest {

    @Value("classpath:instagram-api-definition.json")
    private Resource instagramApiDefinition;

    @Test
    public void shouldExtractTheVersion() throws Exception {
        final SwaggerDefinitionHelper swagger = new SwaggerDefinitionHelper(readResource(instagramApiDefinition));

        assertThat(swagger.getVersion()).isEqualTo("v1");
    }

    @Test
    public void shouldExtractAndManipulateTheName() throws Exception {
        final SwaggerDefinitionHelper swagger = new SwaggerDefinitionHelper(readResource(instagramApiDefinition));

        // the actual value of the api title is 'Instagram API '
        assertThat(swagger.getName()).isEqualTo("instagram-api");
    }

    @Test(expected = SwaggerParseException.class)
    public void shouldThrowExceptionIfVersionCannotBeParsed() throws Exception {
        final SwaggerDefinitionHelper swagger = new SwaggerDefinitionHelper("{\"info\": {\"title\": \"Some Api\"}}");

        swagger.getVersion();
    }

    @Test(expected = SwaggerParseException.class)
    public void shouldThrowExceptionIfNameCannotBeParsed() throws Exception {
        final SwaggerDefinitionHelper swagger = new SwaggerDefinitionHelper("{\"info\": {\"version\": \"1.0\"}}");

        swagger.getName();
    }

    @Test(expected = SwaggerParseException.class)
    public void shouldThrowExceptionIfDefinitionCannotBeParsed() throws Exception {
        new SwaggerDefinitionHelper("{");
    }
}
