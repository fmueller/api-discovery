package org.zalando.apidiscovery.storage.resource;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.zalando.apidiscovery.storage.domain.service.ApiDefinitionProcessingService;
import org.zalando.apidiscovery.storage.domain.service.ApiService;
import org.zalando.apidiscovery.storage.domain.service.ApplicationService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("production")
@WebMvcTest(controllers = {
    ApiDefinitionResourceController.class,
    ApiResourceController.class,
    ApplicationResourceController.class})
public class CorsWithOAuthTestResource {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ApiDefinitionProcessingService apiDefinitionService;

    @MockBean
    private ApiService apiService;

    @MockBean
    private ApplicationService applicationService;

    @Test
    public void shouldSupportCorsWhenOAuthIsEnabledOnAllResources() throws Exception {
        mvc.perform(options("/apis")).andExpect(status().isOk());
        mvc.perform(options("/apis/dummy/versions")).andExpect(status().isOk());
        mvc.perform(options("/apis/dummy/versions/dummy/definitions/dummy")).andExpect(status().isOk());
        mvc.perform(options("/apis/dummy/deployments")).andExpect(status().isOk());
        mvc.perform(options("/applications")).andExpect(status().isOk());
        mvc.perform(options("/api-definitions")).andExpect(status().isOk());
    }
}
