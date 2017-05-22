package org.zalando.apidiscovery.crawler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.stups.tokens.AccessTokens;
import org.zalando.stups.tokens.Tokens;

import java.net.URI;
import java.net.URISyntaxException;


@Configuration
public class OAuthConfiguration {

    @Bean
    public AccessTokens accessTokens() throws URISyntaxException {
        // Uri, ManageToken and addScope are provided as dummy values due to insufficient design of the org.zalando.stups.tokens.AccessTokensBuilder.
        // The values are defined in the deployment sets.
        // AccessTokens are fetched from the filesystem via org.zalando.stups.tokens.fs.FilesystemSecretRefresher
        return Tokens.createAccessTokensWithUri(new URI("https://dummy.zalando.com/"))
            .manageToken("dummy")
            .addScope("dummy")
            .done()
            .start();
    }
}
