package org.zalando.apidiscovery.storage;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    JacksonConfiguration.class
})
public class ResourceComponentTestConfig {
}
