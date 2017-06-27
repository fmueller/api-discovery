package org.zalando.apidiscovery.storage.resource;


import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.apidiscovery.storage.AbstractComponentTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("production")
public class CorsWithOAuthTest extends AbstractComponentTest {

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
