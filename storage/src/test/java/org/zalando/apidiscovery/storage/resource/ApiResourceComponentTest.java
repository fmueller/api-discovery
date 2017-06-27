package org.zalando.apidiscovery.storage.resource;

import org.junit.Test;
import org.zalando.apidiscovery.storage.AbstractComponentTest;
import org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState;
import org.zalando.apidiscovery.storage.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.repository.ApiEntity;
import org.zalando.apidiscovery.storage.repository.ApplicationEntity;

import static java.lang.String.valueOf;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.zalando.apidiscovery.storage.legacy.ApiLifecycleManager.ACTIVE;
import static org.zalando.apidiscovery.storage.legacy.ApiLifecycleManager.DECOMMISSIONED;
import static org.zalando.apidiscovery.storage.legacy.ApiLifecycleManager.INACTIVE;

public class ApiResourceComponentTest extends AbstractComponentTest {

    private final static String TEST_API = "testAPI";
    private final static String ANOTHER_API = "anotherAPI";
    private final static String V1 = "1.0.0";
    private final static String V2 = "2.0.0";
    private final static String APP1 = "app1";
    private final static String APP2 = "app2";

    @Test
    public void shouldReturnAllApis() throws Exception {
        ApplicationEntity app1 = givenApplication(APP1);
        ApplicationEntity app2 = givenApplication(APP2);

        ApiEntity testAPi100 = givenApiEntity(TEST_API, V1);
        ApiEntity testAPi200 = givenApiEntity(TEST_API, V2);
        ApiEntity anotherAPi100 = givenApiEntity(ANOTHER_API, V1);

        ApiDeploymentEntity testAPi100OnApp1 = ApiDeploymentEntity.builder()
            .api(testAPi100)
            .application(app1)
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .build();

        ApiDeploymentEntity testAPi200OnApp1 = ApiDeploymentEntity.builder()
            .api(testAPi200)
            .application(app1)
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .build();

        ApiDeploymentEntity anotherAPi100OnApp2 = ApiDeploymentEntity.builder()
            .api(anotherAPi100)
            .application(app2)
            .lifecycleState(ApiLifecycleState.INACTIVE)
            .build();

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));
        anotherAPi100.setApiDeploymentEntities(asList(anotherAPi100OnApp2));

        apiRepository.save(asList(testAPi100, testAPi200, anotherAPi100));

        mvc.perform(get("/apis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.apis[0].id", equalTo(ANOTHER_API)))
            .andExpect(jsonPath("$.apis[0].lifecycle_state", equalTo(INACTIVE)))
            .andExpect(jsonPath("$.apis[1].id", equalTo(TEST_API)))
            .andExpect(jsonPath("$.apis[1].lifecycle_state", equalTo(ACTIVE)));
    }

    @Test
    public void shouldReturnAllActiveApis() throws Exception {
        ApplicationEntity app1 = givenApplication(APP1);
        ApplicationEntity app2 = givenApplication(APP2);

        ApiEntity testAPi100 = givenApiEntity(TEST_API, V1);
        ApiEntity anotherAPi100 = givenApiEntity(ANOTHER_API, V1);

        ApiDeploymentEntity testAPi100OnApp1 = ApiDeploymentEntity.builder()
            .api(testAPi100)
            .application(app1)
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .build();

        ApiDeploymentEntity anotherAPi100OnApp2 = ApiDeploymentEntity.builder()
            .api(anotherAPi100)
            .application(app2)
            .lifecycleState(ApiLifecycleState.INACTIVE)
            .build();

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        anotherAPi100.setApiDeploymentEntities(asList(anotherAPi100OnApp2));

        apiRepository.save(asList(testAPi100, anotherAPi100));

        mvc.perform(get("/apis?lifecycle_state=ACTIVE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.apis", hasSize(1)))
            .andExpect(jsonPath("$.apis[0].id", equalTo(TEST_API)))
            .andExpect(jsonPath("$.apis[0].lifecycle_state", equalTo(ACTIVE)));
    }

    @Test
    public void shouldReturnAllInActiveApis() throws Exception {
        ApplicationEntity app1 = givenApplication(APP1);
        ApplicationEntity app2 = givenApplication(APP2);

        ApiEntity testAPi100 = givenApiEntity(TEST_API, V1);
        ApiEntity testAPi200 = givenApiEntity(TEST_API, V2);
        ApiEntity anotherAPi100 = givenApiEntity(ANOTHER_API, V1);

        ApiDeploymentEntity testAPi100OnApp1 = ApiDeploymentEntity.builder()
            .api(testAPi100)
            .application(app1)
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .build();

        ApiDeploymentEntity testAPi200OnApp1 = ApiDeploymentEntity.builder()
            .api(testAPi200)
            .application(app1)
            .lifecycleState(ApiLifecycleState.INACTIVE)
            .build();

        ApiDeploymentEntity anotherAPi100OnApp2 = ApiDeploymentEntity.builder()
            .api(anotherAPi100)
            .application(app2)
            .lifecycleState(ApiLifecycleState.INACTIVE)
            .build();

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));
        anotherAPi100.setApiDeploymentEntities(asList(anotherAPi100OnApp2));

        apiRepository.save(asList(testAPi100, testAPi200, anotherAPi100));

        mvc.perform(get("/apis?lifecycle_state=INACTIVE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.apis", hasSize(1)))
            .andExpect(jsonPath("$.apis[0].id", equalTo(ANOTHER_API)))
            .andExpect(jsonPath("$.apis[0].lifecycle_state", equalTo(INACTIVE)));
    }

    @Test
    public void shouldReturnAllDecommissionedApis() throws Exception {
        ApplicationEntity app1 = givenApplication(APP1);
        ApplicationEntity app2 = givenApplication(APP2);

        ApiEntity testAPi100 = givenApiEntity(TEST_API, V1);
        ApiEntity anotherAPi100 = givenApiEntity(ANOTHER_API, V1);

        ApiDeploymentEntity testAPi100OnApp1 = ApiDeploymentEntity.builder()
            .api(testAPi100)
            .application(app1)
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .build();

        ApiDeploymentEntity anotherAPi100OnApp2 = ApiDeploymentEntity.builder()
            .api(anotherAPi100)
            .application(app2)
            .lifecycleState(ApiLifecycleState.DECOMMISSIONED)
            .build();

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        anotherAPi100.setApiDeploymentEntities(asList(anotherAPi100OnApp2));

        apiRepository.save(asList(testAPi100, anotherAPi100));

        mvc.perform(get("/apis?lifecycle_state=DECOMMISSIONED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.apis", hasSize(1)))
            .andExpect(jsonPath("$.apis[0].id", equalTo(ANOTHER_API)))
            .andExpect(jsonPath("$.apis[0].lifecycle_state", equalTo(DECOMMISSIONED)));
    }

    @Test
    public void shouldReturnOneApi() throws Exception {
        ApplicationEntity app1 = givenApplication(APP1);
        ApplicationEntity app2 = givenApplication(APP2);

        ApiEntity testAPi100 = givenApiEntity(TEST_API, V1);
        testAPi100.setApiDeploymentEntities(asList(givenApiDeployment(testAPi100, app1),
            givenApiDeployment(testAPi100, app2)));

        ApiEntity testAPi200 = givenApiEntity(TEST_API, V2);
        testAPi200.setApiDeploymentEntities(asList(givenApiDeployment(testAPi200, app1)));

        apiRepository.save(asList(testAPi100, testAPi200));

        mvc.perform(get("/apis/" + TEST_API))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.api_meta_data.id", equalTo(TEST_API)))
            .andExpect(jsonPath("$.api_meta_data.lifecycle_state", equalTo(ACTIVE)))
            .andExpect(jsonPath("$.versions[*].api_version", hasItems(V1, V2)))
            .andExpect(jsonPath("$.applications[*].name", hasItems(APP1, APP1)))
            .andExpect(jsonPath("$.versions..applications[*].href",
                hasItems(localUriBuilder()
                        .path("applications/app1")
                        .toUriString(),
                    localUriBuilder()
                        .path("applications/app2")
                        .toUriString())))
            .andExpect(jsonPath("$.applications..definitions[*].href",
                hasItems(localUriBuilder()
                        .path("apis/testAPI/versions/1.0.0/definitions/" + valueOf(testAPi100.getDefinitionId()))
                        .toUriString(),
                    localUriBuilder()
                        .path("apis/testAPI/versions/2.0.0/definitions/" + valueOf(testAPi200.getDefinitionId()))
                        .toUriString())));
    }

    @Test
    public void shouldReturn400IfNoApiFound() throws Exception {
        mvc.perform(get("/apis/" + TEST_API))
            .andExpect(status().isNotFound());
    }

    private ApiDeploymentEntity givenApiDeployment(ApiEntity apiEntity, ApplicationEntity applicationEntity) {
        return ApiDeploymentEntity.builder()
            .api(apiEntity)
            .application(applicationEntity)
            .apiUi("/ui")
            .apiUrl("/api")
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .created(now(UTC))
            .lastCrawled(now(UTC))
            .build();
    }

    private ApiEntity givenApiEntity(String name, String version) {
        return ApiEntity.builder()
            .apiName(name)
            .apiVersion(version)
            .definitionHash("1")
            .definitionType("swagger")
            .created(now(UTC))
            .build();
    }

    private ApplicationEntity givenApplication(String name) {
        return applicationRepository.save(
            ApplicationEntity
                .builder()
                .name(name)
                .appUrl("/info")
                .created(now(UTC))
                .build());
    }
}
