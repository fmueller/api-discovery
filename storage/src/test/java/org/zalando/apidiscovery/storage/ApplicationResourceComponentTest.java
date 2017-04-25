package org.zalando.apidiscovery.storage;

import org.junit.Test;
import org.zalando.apidiscovery.storage.api.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.ApiEntity;
import org.zalando.apidiscovery.storage.api.ApiLifecycleState;
import org.zalando.apidiscovery.storage.api.ApplicationEntity;

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

public class ApplicationResourceComponentTest extends AbstractComponentTest {

    @Test
    public void shouldReturnAllApplications() throws Exception {
        ApplicationEntity applicationEntity1 = createApplication("application1");
        ApiEntity apiEntity = createApiEntity("api1", "v1", 1);
        createApiDeployment(apiEntity, applicationEntity1);

        mvc.perform(get("/applications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.applications", hasSize(1)))
            .andExpect(jsonPath("$.applications[0].name", equalTo("application1")))
            .andExpect(jsonPath("$.applications[0].app_url", equalTo("/info")))
            .andExpect(jsonPath("$.applications[0].definitions[0].api_ui", equalTo("/ui")))
            .andExpect(jsonPath("$.applications[0].definitions[0].api_url", equalTo("/url")))
            .andExpect(jsonPath("$.applications[0].definitions[0].href", endsWith("apis/api1/versions/v1/definitions/" + apiEntity.getDefinitionId())))
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
        ApiEntity apiEntity = createApiEntity("api1", "v1", 1);
        createApiDeployment(apiEntity, applicationEntity);

        mvc.perform(get("/applications/application1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", equalTo("application1")))
            .andExpect(jsonPath("$.app_url", equalTo("/info")))
            .andExpect(jsonPath("$.definitions[0].api_ui", equalTo("/ui")))
            .andExpect(jsonPath("$.definitions[0].api_url", equalTo("/url")))
            .andExpect(jsonPath("$.definitions[0].href", endsWith("apis/api1/versions/v1/definitions/" + apiEntity.getDefinitionId())))
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

    private ApiEntity createApiEntity(String name, String version, int definitionId) {
        return ApiEntity.builder()
            .apiName(name)
            .apiVersion(version)
            .definitionId(definitionId)
            .definitionType("swagger")
            .definitionHash("hash")
            .created(now(UTC))
            .build();
    }
}
