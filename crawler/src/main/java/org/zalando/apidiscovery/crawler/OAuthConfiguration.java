package org.zalando.apidiscovery.crawler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.stups.tokens.Tokens;

import java.net.URI;
import java.net.URISyntaxException;


@Configuration
public class OAuthConfiguration {

    @Value("${tokens.access-token-uri}")
    private String accessTokenUri;

    @Bean
    public AccessTokens accessTokens() throws URISyntaxException {
        // ManageToken and addScope are provided as dummy values due to weakness of the builder pattern.
        // TokenIds and scopes are defined in the deployment sets.
        // AccessTokens get fetched from the filesystem via org.zalando.stups.tokens.fs.FilesystemSecretRefresher
        return Tokens.createAccessTokensWithUri(new URI(accessTokenUri))
            .manageToken("dummy")
            .addScope("dummy")
            .done()
            .start();
    }
}
