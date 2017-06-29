package org.zalando.apidiscovery.storage.resource;

import org.junit.Test;
import org.zalando.apidiscovery.storage.AbstractResourceComponentTest;
import org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState;
import org.zalando.apidiscovery.storage.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.repository.ApiEntity;
import org.zalando.apidiscovery.storage.repository.ApplicationEntity;

import java.util.ArrayList;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

public class ApiDeploymentResourceResourceComponentTest extends AbstractResourceComponentTest {

    @Test
    public void shouldReturnAllDeployments() throws Exception {
        ApplicationEntity application = createApplication("application1");
        ApiEntity api1 = createApiEntity("api1", "v1", "api1", 1);
        ApiEntity api1_1 = createApiEntity("api1", "v1", "api1_1", 2);
        ApiEntity api2 = createApiEntity("api1", "v2", "api2", 1);

        createApiDeployment(api1, application);
        createApiDeployment(api1_1, application);
        createApiDeployment(api2, application);

        String expectedApplicationLink = newInstance()
            .fromPath("applications/application1")
            .toUriString();

        mvc.perform(get("/apis/api1/deployments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deployments", hasSize(3)))
            .andExpect(jsonPath("$..api_version", containsInAnyOrder("v1", "v1", "v2")))
            .andExpect(jsonPath("$.deployments[0].application.name", equalTo("application1")))
            .andExpect(jsonPath("$..definition.href",
                containsInAnyOrder(
                    endsWith(expectedDefinitionHref("v1", api1.getDefinitionId())),
                    endsWith(expectedDefinitionHref("v1", api1_1.getDefinitionId())),
                    endsWith(expectedDefinitionHref("v2", api2.getDefinitionId()))
                )))
            .andExpect(jsonPath("$..application.href",
                contains(
                    endsWith(expectedApplicationLink),
                    endsWith(expectedApplicationLink),
                    endsWith(expectedApplicationLink)
                )));
    }

    @Test
    public void shouldReturnEmptyList() throws Exception {
        mvc.perform(get("/apis/UNKNOWN/deployments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deployments", hasSize(0)));
    }

    private String expectedDefinitionHref(String version, long apiDefinitionId) {
        return newInstance()
            .path("apis/api1/versions/" + version + "/definitions/" + apiDefinitionId)
            .toUriString();
    }

    private ApiDeploymentEntity createApiDeployment(ApiEntity apiEntity, ApplicationEntity applicationEntity) {
        ApiDeploymentEntity apiDeploymentEntity = ApiDeploymentEntity.builder()
            .api(apiEntity)
            .application(applicationEntity)
            .apiUi("/ui")
            .apiUrl("/api")
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .created(now(UTC))
            .lastCrawled(now(UTC))
            .build();

        apiEntity.getApiDeploymentEntities().add(apiDeploymentEntity);
        apiRepository.save(apiEntity);
        return apiDeploymentEntity;
    }

    private ApiEntity createApiEntity(String name, String version, String hash, int definitionId) {
        return ApiEntity.builder()
            .apiName(name)
            .apiVersion(version)
            .definitionHash(hash)
            .definitionId(definitionId)
            .definitionType("swagger")
            .created(now(UTC))
            .apiDeploymentEntities(new ArrayList<>())
            .build();
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
}