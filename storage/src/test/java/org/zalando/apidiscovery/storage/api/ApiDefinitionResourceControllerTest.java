package org.zalando.apidiscovery.storage.api;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

@RunWith(MockitoJUnitRunner.class)
public class ApiDefinitionResourceControllerTest {

    private ApiDefinitionResourceController apiDefinitionController;

    @Mock
    private ApiDefinitionProcessingService apiDefinitionProcessingService;


    @Before
    public void setUp() throws Exception {
        apiDefinitionController = new ApiDefinitionResourceController(apiDefinitionProcessingService);
    }

    @Test
    @Ignore
    public void shouldReturnHttpCodeCreated() throws Exception {
        doNothing().when(apiDefinitionProcessingService).processCrawledApiDefinition(any(CrawledApiDefinitionDto.class));

        ResponseEntity<Void> response = apiDefinitionController.postCrawledApiDefinition(new CrawledApiDefinitionDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
