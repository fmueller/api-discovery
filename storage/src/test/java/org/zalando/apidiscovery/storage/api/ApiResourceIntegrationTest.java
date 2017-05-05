package org.zalando.apidiscovery.storage.api;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.apidiscovery.storage.api.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;
import org.zalando.apidiscovery.storage.api.domain.model.ApiLifecycleState;
import org.zalando.apidiscovery.storage.api.repository.ApplicationEntity;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static java.lang.String.valueOf;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;
import static org.zalando.apidiscovery.storage.legacy.ApiLifecycleManager.ACTIVE;
import static org.zalando.apidiscovery.storage.legacy.ApiLifecycleManager.DECOMMISSIONED;
import static org.zalando.apidiscovery.storage.legacy.ApiLifecycleManager.INACTIVE;

public class ApiResourceIntegrationTest extends AbstractResourceIntegrationTest {

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

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis", String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.apis[0].api_meta_data.name", equalTo(ANOTHER_API)));
        assertThat(response, hasJsonPath("$.apis[0].api_meta_data.lifecycle_state", equalTo(INACTIVE)));
        assertThat(response, hasJsonPath("$.apis[1].api_meta_data.name", equalTo(TEST_API)));
        assertThat(response, hasJsonPath("$.apis[1].api_meta_data.lifecycle_state", equalTo(ACTIVE)));
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

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis?lifecycle_state=ACTIVE", String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.apis", hasSize(1)));
        assertThat(response, hasJsonPath("$.apis[0].api_meta_data.name", equalTo(TEST_API)));
        assertThat(response, hasJsonPath("$.apis[0].api_meta_data.lifecycle_state", equalTo(ACTIVE)));
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

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis?lifecycle_state=INACTIVE", String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.apis", hasSize(1)));
        assertThat(response, hasJsonPath("$.apis[0].api_meta_data.name", equalTo(ANOTHER_API)));
        assertThat(response, hasJsonPath("$.apis[0].api_meta_data.lifecycle_state", equalTo(INACTIVE)));
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

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis?lifecycle_state=DECOMMISSIONED", String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.apis", hasSize(1)));
        assertThat(response, hasJsonPath("$.apis[0].api_meta_data.name", equalTo(ANOTHER_API)));
        assertThat(response, hasJsonPath("$.apis[0].api_meta_data.lifecycle_state", equalTo(DECOMMISSIONED)));
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

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis/" + TEST_API, String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));

        final String response = responseEntity.getBody();

        assertThat(response, isJson(allOf(
            withJsonPath("$.api_meta_data.name", equalTo(TEST_API)),
            withJsonPath("$.api_meta_data.lifecycle_state", equalTo(ACTIVE)),
            withJsonPath("$.versions[*].api_version", hasItems(V1, V2)),
            withJsonPath("$.applications[*].name", hasItems(APP1, APP1)))));

        assertThat(response,
            hasJsonPath("$.versions..applications[*].href",
                hasItems(localUriBuilder()
                        .path("applications/app1")
                        .toUriString(),
                    localUriBuilder()
                        .path("applications/app2")
                        .toUriString())));

        assertThat(response,
            hasJsonPath("$.applications..definitions[*].href",
                hasItems(localUriBuilder()
                        .path("apis/testAPI/versions/1.0.0/definitions/" + valueOf(testAPi100.getDefinitionId()))
                        .toUriString(),
                    localUriBuilder()
                        .path("apis/testAPI/versions/2.0.0/definitions/" + valueOf(testAPi200.getDefinitionId()))
                        .toUriString())));

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

    @Test
    public void shouldReturn400IfNoApiFound() throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis/" + TEST_API, String.class);
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

}
