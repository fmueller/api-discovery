package org.zalando.apidiscovery.storage;

import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.zalando.apidiscovery.storage.api.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.ApiEntity;
import org.zalando.apidiscovery.storage.api.ApiLifecycleState;
import org.zalando.apidiscovery.storage.api.ApplicationEntity;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.zalando.apidiscovery.storage.ApiLifecycleManager.ACTIVE;

public class ApplicationResourceIntegrationTest extends AbstractResourceIntegrationTest {

    @Test
    public void shouldReturnAllApplications() throws Exception {
        givenApplication("application1");
        givenApplication("application2");

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/applications/", String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(OK));
        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.applications", hasSize(2)));
    }

    @Test
    public void shouldReturnEmptyApplicationList() throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/applications/", String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(OK));
        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.applications", hasSize(0)));
    }

    @Test
    public void shouldReturnAllApplicationsWithDeployments() throws Exception {
        ApplicationEntity applicationEntity = givenApplication("application1");
        ApiEntity apiEntity = givenApiEntity("api1", "v1");
        ApiDeploymentEntity apiDeploymentEntity = givenApiDeployment(apiEntity, applicationEntity);
        apiEntity.setApiDeploymentEntities(asList(apiDeploymentEntity));
        apiRepository.save(apiEntity);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/applications/", String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(OK));
        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.applications", hasSize(1)));
        final String expectedHref = localUriBuilder()
            .path("apis/api1/versions/v1/definitions/" + apiEntity.getId())
            .toUriString();
        assertThat(response, isJson(allOf(
            withJsonPath("$.applications[0].name", equalTo("application1")),
            withJsonPath("$.applications[0].app_url", equalTo("/info")),
            withJsonPath("$.applications[0].definitions[0].api_ui", equalTo("/ui")),
            withJsonPath("$.applications[0].definitions[0].api_url", equalTo("/url")),
            withJsonPath("$.applications[0].definitions[0].href", equalTo(expectedHref)),
            withJsonPath("$.applications[0].definitions[0].lifecycle_state", equalTo(ACTIVE)))));
    }

    @Test
    public void shouldReturnOneApplication() throws Exception {
        ApplicationEntity applicationEntity = givenApplication("application1");
        ApiEntity apiEntity = givenApiEntity("api1", "v1");
        ApiDeploymentEntity apiDeploymentEntity = givenApiDeployment(apiEntity, applicationEntity);
        apiEntity.setApiDeploymentEntities(asList(apiDeploymentEntity));
        apiRepository.save(apiEntity);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/applications/application1", String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(OK));
        final String response = responseEntity.getBody();
        final String expectedHref = localUriBuilder()
            .path("apis/api1/versions/v1/definitions/" + apiEntity.getId())
            .toUriString();
        assertThat(response, isJson(allOf(
            withJsonPath("$.name", equalTo("application1")),
            withJsonPath("$.app_url", equalTo("/info")),
            withJsonPath("$.definitions[0].api_ui", equalTo("/ui")),
            withJsonPath("$.definitions[0].api_url", equalTo("/url")),
// fails because of #101
//            withJsonPath("$.created", equalTo(applicationEntity.getCreated().toString())),
//            withJsonPath("$.definitions[0].created", equalTo(apiDeploymentEntity.getCreated().toString())),
//            withJsonPath("$.definitions[0].last_updated", equalTo(apiDeploymentEntity.getLastCrawled().toString())),
            withJsonPath("$.definitions[0].href", equalTo(expectedHref)),
            withJsonPath("$.definitions[0].lifecycle_state", equalTo(ACTIVE)))));
    }

    @Test
    public void shouldReturn404ApplicationNotFound() throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/applications/application1", String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(NOT_FOUND));
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

    private ApiDeploymentEntity givenApiDeployment(ApiEntity apiEntity, ApplicationEntity applicationEntity) {
        return ApiDeploymentEntity.builder()
            .api(apiEntity)
            .application(applicationEntity)
            .apiUi("/ui")
            .apiUrl("/url")
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .created(now(UTC))
            .lastCrawled(now(UTC))
            .build();
    }

    private ApiEntity givenApiEntity(String name, String version) {
        return ApiEntity.builder()
            .apiName(name)
            .apiVersion(version)
            .definitionType("swagger")
            .created(now(UTC))
            .build();
    }
}
