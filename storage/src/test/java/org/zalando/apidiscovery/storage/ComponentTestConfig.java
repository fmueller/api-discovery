package org.zalando.apidiscovery.storage;


import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@TestConfiguration
@Import(value = {JacksonConfiguration.class, MvcConfiguration.class})
@EnableWebMvc
@AutoConfigureDataJpa
public class ComponentTestConfig {

    @Bean
    public MockMvc mockMvc(final WebApplicationContext context) throws Exception {
        return MockMvcBuilders
            .webAppContextSetup(context)
            .build();
    }

}
