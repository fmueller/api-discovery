package org.zalando.apidiscovery.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.zalando.apidiscovery.storage.api.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.ApiEntity;
import org.zalando.apidiscovery.storage.api.ApiLifecycleState;
import org.zalando.apidiscovery.storage.api.ApiRepository;
import org.zalando.apidiscovery.storage.api.ApiService;
import org.zalando.apidiscovery.storage.api.ApplicationEntity;
import org.zalando.apidiscovery.storage.api.ApplicationRepository;
import org.zalando.apidiscovery.storage.api.ApplicationResourceController;
import org.zalando.apidiscovery.storage.api.ApplicationService;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.zalando.apidiscovery.storage.ApiLifecycleManager.ACTIVE;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@WebAppConfiguration
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
public class ApplicationResourceComponentTest {

    @Autowired
    protected ApplicationRepository applicationRepository;

    @Autowired
    protected ApiRepository apiRepository;

    @Autowired
    private MockMvc mvc;

    @Configuration
    @Import(value = {JacksonConfiguration.class, MvcConfiguration.class, ApplicationResourceController.class})
    @EnableWebMvc
    @AutoConfigureDataJpa
    static class TestConfig {

        @Bean
        public MockMvc mockMvc(final WebApplicationContext context) throws Exception {
            return MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        }

        @Bean
        public ApplicationService applicationService(ApplicationRepository applicationRepository) {
            return new ApplicationService((applicationRepository));
        }

        @Bean
        public ApiService apiService(ApiRepository apiRepository, ApplicationService applicationService) {
            return new ApiService(apiRepository, applicationService);
        }

    }

    @Test
    public void shouldReturnAllApplications() throws Exception {
        ApplicationEntity applicationEntity1 = createApplication("application1");
        ApiEntity apiEntity = createApiEntity("api1", "v1", "hash");
        createApiDeployment(apiEntity, applicationEntity1);

        mvc.perform(get("/applications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.applications", hasSize(1)))
            .andExpect(jsonPath("$.applications[0].name", equalTo("application1")))
            .andExpect(jsonPath("$.applications[0].app_url", equalTo("/info")))
            .andExpect(jsonPath("$.applications[0].definitions[0].api_ui", equalTo("/ui")))
            .andExpect(jsonPath("$.applications[0].definitions[0].api_url", equalTo("/url")))
            .andExpect(jsonPath("$.applications[0].definitions[0].href", endsWith("apis/api1/versions/v1/definitions/" + apiEntity.getId())))
            .andExpect(jsonPath("$.applications[0].definitions[0].lifecycle_state", equalTo(ACTIVE)));
    }

    @Test
    public void shouldReturnEmptyApplicationList() throws Exception {
        mvc.perform(get("/applications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.applications", hasSize(0)));
    }

    @Test
    public void shouldReturnOneApplication() throws Exception {
        ApplicationEntity applicationEntity = createApplication("application1");
        ApiEntity apiEntity = createApiEntity("api1", "v1", "hash");
        createApiDeployment(apiEntity, applicationEntity);

        mvc.perform(get("/applications/application1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", equalTo("application1")))
            .andExpect(jsonPath("$.app_url", equalTo("/info")))
            .andExpect(jsonPath("$.definitions[0].api_ui", equalTo("/ui")))
            .andExpect(jsonPath("$.definitions[0].api_url", equalTo("/url")))
            .andExpect(jsonPath("$.definitions[0].href", endsWith("apis/api1/versions/v1/definitions/" + apiEntity.getId())))
            .andExpect(jsonPath("$.definitions[0].lifecycle_state", equalTo(ACTIVE)));
    }

    @Test
    public void shouldReturn404ApplicationNotFound() throws Exception {
        mvc.perform(get("/applications/app"))
            .andExpect(status().isNotFound());
    }


    private ApplicationEntity createApplication(String name) {
        return applicationRepository.save(
            ApplicationEntity
                .builder()
                .name(name)
                .appUrl("/info")
                .created(now(UTC))
                .build());
    }

    private ApiDeploymentEntity createApiDeployment(ApiEntity apiEntity, ApplicationEntity applicationEntity) {
        ApiDeploymentEntity apiDeploymentEntity = ApiDeploymentEntity.builder()
            .api(apiEntity)
            .application(applicationEntity)
            .apiUi("/ui")
            .apiUrl("/url")
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .created(now(UTC))
            .lastCrawled(now(UTC))
            .build();

        apiEntity.setApiDeploymentEntities(asList(apiDeploymentEntity));
        apiRepository.save(apiEntity);
        return apiDeploymentEntity;
    }

    private ApiEntity createApiEntity(String name, String version, String definitionHash) {
        return ApiEntity.builder()
            .apiName(name)
            .apiVersion(version)
            .definitionHash(definitionHash)
            .definitionType("swagger")
            .created(now(UTC))
            .build();
    }
}
