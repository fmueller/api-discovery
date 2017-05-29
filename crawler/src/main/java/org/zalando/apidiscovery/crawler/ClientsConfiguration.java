package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.zalando.apidiscovery.crawler.gateway.ApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.LegacyApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.WellKnownSchemaGateway;
import org.zalando.stups.clients.kio.KioOperations;
import org.zalando.stups.clients.kio.spring.RestTemplateKioOperations;
import org.zalando.stups.oauth2.spring.client.StupsOAuth2RestTemplate;
import org.zalando.stups.oauth2.spring.client.StupsTokensAccessTokenProvider;
import org.zalando.stups.spring.http.client.ClientHttpRequestFactorySelector;
import org.zalando.stups.spring.http.client.TimeoutConfig;
import org.zalando.stups.tokens.AccessTokens;

import java.nio.charset.Charset;

@Configuration
public class ClientsConfiguration {

    @Autowired
    private AccessTokens accessTokens;

    @Autowired
    private ObjectMapper objectMapper;

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
    public LegacyApiDiscoveryStorageGateway legacyStorageOperations(@Value("${storage.url}") String storageBaseUrl) {
        return new LegacyApiDiscoveryStorageGateway(buildOAuth2RestTemplate("storage"), storageBaseUrl);
    }

    @Bean
    public ApiDiscoveryStorageGateway storageOperations(@Value("${storage.url}") String storageBaseUrl) {
        return new ApiDiscoveryStorageGateway(buildOAuth2RestTemplate("storage"), storageBaseUrl);
    }

    @Bean
    public WellKnownSchemaGateway wellKnownSchemaGateway() {
        return new WellKnownSchemaGateway(oauth2Operations());
    }

    @Bean
    public RestTemplate oauth2Operations() {
        RestTemplate restTemplate = buildOAuth2RestTemplate("schema");
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    private RestTemplate buildOAuth2RestTemplate(final String tokenName) {
        RestTemplate rt = new StupsOAuth2RestTemplate(new StupsTokensAccessTokenProvider(tokenName, accessTokens),
                ClientHttpRequestFactorySelector.getRequestFactory(new TimeoutConfig.Builder()
                        .withReadTimeout(readTimeout)
                        .withConnectTimeout(connectTimeout)
                        .withConnectionRequestTimeout(connectionRequestTimeout)
                        .build()));
        rt.getMessageConverters().add(0, new MappingJackson2HttpMessageConverter(objectMapper));
        return rt;
    }

}
