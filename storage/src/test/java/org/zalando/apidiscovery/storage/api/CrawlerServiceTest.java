package org.zalando.apidiscovery.storage.api;

import org.junit.Test;
import org.zalando.apidiscovery.storage.utils.SwaggerDefinitionHelper;
import org.zalando.apidiscovery.storage.utils.SwaggerParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class CrawlerServiceTest {

    @Test
    public void shouldSetNameAndVersionFromTheSwaggerDefinition() throws Exception {
        final ApiDefinitionManager apiDefinitionManager = new ApiDefinitionManager(null, null, null, new SwaggerDefinitionHelper());
        final CrawledApiDefinitionDto apiDef = CrawledApiDefinitionDto.builder()
                .definition("{\"info\": {\"title\": \"Api Name\", \"version\": \"1.0.0\"}}")
                .build();

        apiDefinitionManager.setApiNameAndVersion(apiDef);

        assertThat(apiDef.getApiName()).isEqualTo("api-name");
        assertThat(apiDef.getVersion()).isEqualTo("1.0.0");
    }

    @Test(expected = SwaggerParseException.class)
    public void shouldThrowAnExceptionIfCannotParseSwaggerDefinition() throws Exception {
        final ApiDefinitionManager apiDefinitionManager = new ApiDefinitionManager(null, null, null, new SwaggerDefinitionHelper());
        final CrawledApiDefinitionDto apiDef = CrawledApiDefinitionDto.builder()
                .definition("{\"info\": \"here should be actually a sub-document with version and title of the api\"}")
                .build();

        apiDefinitionManager.setApiNameAndVersion(apiDef);
    }
}
