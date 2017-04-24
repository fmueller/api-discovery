package org.zalando.apidiscovery.crawler;

import org.junit.Test;
import org.zalando.stups.clients.kio.ApplicationBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.crawler.TestDataHelper.metaApiKioApplication;

public class KioApplicationTest {

    @Test
    public void shouldExtractServiceUrl() throws Exception {
        String serviceUrl = metaApiKioApplication().getServiceUrl();

        assertThat(serviceUrl).isEqualTo("https://meta.api/");
    }

    @Test
    public void shouldBeEqualToAnotherKioApplicationWithTheSameApplicationBase() throws Exception {
        KioApplication app1 = new KioApplication(new ApplicationBase());
        KioApplication app2 = new KioApplication(new ApplicationBase());
        KioApplication nullApp1 = new KioApplication(null);
        KioApplication nullApp2 = new KioApplication(null);

        assertThat(app1).isEqualTo(app2);
        assertThat(nullApp1).isEqualTo(nullApp2);
        assertThat(app1).isNotEqualTo(nullApp1);
        assertThat(nullApp1).isNotEqualTo(app1);
    }
}
