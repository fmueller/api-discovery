package org.zalando.apidiscovery.crawler;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.zalando.apidiscovery.crawler.storage.ApiDiscoveryStorageClient;
import org.zalando.stups.clients.kio.KioOperations;
import org.zalando.stups.clients.kio.spring.RestTemplateKioOperations;
import org.zalando.stups.oauth2.spring.client.StupsOAuth2RestTemplate;
import org.zalando.stups.oauth2.spring.client.StupsTokensAccessTokenProvider;
import org.zalando.stups.spring.http.client.ClientHttpRequestFactorySelector;
import org.zalando.stups.spring.http.client.TimeoutConfig;
import org.zalando.stups.tokens.AccessTokens;

@Configuration
public class ClientsConfiguration {

    @Autowired
    private AccessTokens accessTokens;

    @Value("${crawler.timeout.read}")
    private int readTimeout = 5000;

    @Value("${crawler.timeout.connect}")
    private int connectTimeout = 2000;

    @Value("${crawler.timeout.connection-request}")
    private int connectionRequestTimeout = 1000;

    @Bean
    public KioOperations kioOperations(@Value("${kio.url}") String kioBaseUrl) {
        return new RestTemplateKioOperations(buildOAuth2RestTemplate("kio"), kioBaseUrl);
    }

    @Bean
    public ApiDiscoveryStorageClient storageOperations(@Value("${storage.url}") String storageBaseUrl) {
        return new ApiDiscoveryStorageClient(buildOAuth2RestTemplate("storage"), storageBaseUrl);
    }

    @Bean
    public RestTemplate oauth2Operations() {
        RestTemplate restTemplate = buildOAuth2RestTemplate("schema");
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    private RestTemplate buildOAuth2RestTemplate(final String tokenName) {
        return new StupsOAuth2RestTemplate(new StupsTokensAccessTokenProvider(tokenName, accessTokens),
                ClientHttpRequestFactorySelector.getRequestFactory(new TimeoutConfig.Builder()
                        .withReadTimeout(readTimeout)
                        .withConnectTimeout(connectTimeout)
                        .withConnectionRequestTimeout(connectionRequestTimeout)
                        .build()));
    }
}
