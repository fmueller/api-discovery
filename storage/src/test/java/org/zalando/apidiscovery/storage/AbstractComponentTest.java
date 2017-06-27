package org.zalando.apidiscovery.storage;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.apidiscovery.storage.repository.ApiRepository;
import org.zalando.apidiscovery.storage.repository.ApplicationRepository;

import javax.persistence.EntityManager;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ComponentScan(basePackageClasses = Application.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@Import(value = AbstractComponentTest.ComponentTestConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
public abstract class AbstractComponentTest {

    @Autowired
    protected ApplicationRepository applicationRepository;

    @Autowired
    protected ApiRepository apiRepository;

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected MockMvc mvc;

    @TestConfiguration
    static class ComponentTestConfig {

        @Bean
        public MockMvc mockMvc(final WebApplicationContext context) throws Exception {
            return MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        }
    }

    protected UriComponentsBuilder localUriBuilder() {
        return UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost");
    }
}
