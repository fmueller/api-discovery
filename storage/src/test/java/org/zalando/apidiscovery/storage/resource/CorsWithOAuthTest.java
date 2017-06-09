package org.zalando.apidiscovery.storage.resource;


import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.apidiscovery.storage.AbstractResourceIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("production")
public class CorsWithOAuthTest extends AbstractResourceIntegrationTest {

    @Test
    public void shouldSupportCorsWhenOAuthIsEnabledOnAllResources() {
        assertThat(optionsRequest("/apis")).isEqualTo(HttpStatus.OK);
        assertThat(optionsRequest("/apis/dummy/versions")).isEqualTo(HttpStatus.OK);
        assertThat(optionsRequest("/apis/dummy/versions/dummy/definitions/dummy")).isEqualTo(HttpStatus.OK);
        assertThat(optionsRequest("/apis/dummy/deployments")).isEqualTo(HttpStatus.OK);
        assertThat(optionsRequest("/applications")).isEqualTo(HttpStatus.OK);
        assertThat(optionsRequest("/api-definitions")).isEqualTo(HttpStatus.OK);
    }

    private HttpStatus optionsRequest(String url) {
        return restTemplate.exchange(url, HttpMethod.OPTIONS, RequestEntity.EMPTY, String.class).getStatusCode();
    }
}
