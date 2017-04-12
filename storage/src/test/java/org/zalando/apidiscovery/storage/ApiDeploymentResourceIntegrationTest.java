package org.zalando.apidiscovery.storage;

import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.zalando.apidiscovery.storage.api.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.ApiEntity;
import org.zalando.apidiscovery.storage.api.ApiLifecycleState;
import org.zalando.apidiscovery.storage.api.ApplicationEntity;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class ApiDeploymentResourceIntegrationTest extends AbstractResourceIntegrationTest {

    @Test
    public void shouldReturnAllDeployments() throws Exception {
        ApplicationEntity application = givenApplication("application1");
        ApiEntity api1 = givenApiEntity("api1", "v1");
        ApiEntity api1_1 = givenApiEntity("api1", "v1");
        ApiEntity api2 = givenApiEntity("api1", "v2");

        givenApiDeployment(api1, application);
        givenApiDeployment(api1_1, application);
        givenApiDeployment(api2, application);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis/api1/deployments", String.class);

        final String response = responseEntity.getBody();
        final String expectedApplicationLink = localUriBuilder()
            .path("applications/application1")
            .toUriString();

        assertThat(response, hasJsonPath("$.deployments", hasSize(3)));
        assertThat(response, hasJsonPath("$..api_version", containsInAnyOrder("v1", "v1", "v2")));
        assertThat(response, hasJsonPath("$.deployments[0].application.name", equalTo("application1")));
        assertThat(response, hasJsonPath("$..definition.href",
            containsInAnyOrder(
                expectedDefinitionHref("v1", api1.getId()),
                expectedDefinitionHref("v1", api1_1.getId()),
                expectedDefinitionHref("v2", api2.getId())
            )));
        assertThat(response, hasJsonPath("$..application.href",
            contains(
                expectedApplicationLink,
                expectedApplicationLink,
                expectedApplicationLink
            )));
    }

    private String expectedDefinitionHref(String version, long apiDefinitionId) {
        return localUriBuilder()
            .path("apis/api1/versions/" + version + "/definitions/" + apiDefinitionId)
            .toUriString();
    }

    private ApiDeploymentEntity givenApiDeployment(ApiEntity apiEntity, ApplicationEntity applicationEntity) {
        ApiDeploymentEntity apiDeploymentEntity = ApiDeploymentEntity.builder()
            .api(apiEntity)
            .application(applicationEntity)
            .apiUi("/ui")
            .apiUrl("/api")
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .created(now(UTC))
            .lastCrawled(now(UTC))
            .build();

        apiEntity.setApiDeploymentEntities(asList(apiDeploymentEntity));
        apiRepository.save(apiEntity);
        return apiDeploymentEntity;
    }

    private ApiEntity givenApiEntity(String name, String version) {
        return ApiEntity.builder()
            .apiName(name)
            .apiVersion(version)
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