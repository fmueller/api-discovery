package org.zalando.apidiscovery.storage;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.apidiscovery.storage.api.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.ApiEntity;
import org.zalando.apidiscovery.storage.api.ApplicationEntity;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zalando.apidiscovery.storage.DomainObjectGen.API_NAME;
import static org.zalando.apidiscovery.storage.DomainObjectGen.API_UI;
import static org.zalando.apidiscovery.storage.DomainObjectGen.API_URL;
import static org.zalando.apidiscovery.storage.DomainObjectGen.API_VERSION_1;
import static org.zalando.apidiscovery.storage.DomainObjectGen.APP1_NAME;
import static org.zalando.apidiscovery.storage.DomainObjectGen.DEFINITION;
import static org.zalando.apidiscovery.storage.DomainObjectGen.DEFINITION_TYPE;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApiDeployment;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApiEntity;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApplication;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.ACTIVE;

public class ApiDefinitionResourceIntegrationTest extends AbstractResourceIntegrationTest {

    @Test
    public void shouldReturnOneApiDefinition() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));

        apiRepository.save(asList(testAPi100));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis/" + API_NAME + "/versions/" + API_VERSION_1 + "/definitions/" + testAPi100.getId(), String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.id", equalTo(String.valueOf(testAPi100.getId()))));
        assertThat(response, hasJsonPath("$.type", equalTo(DEFINITION_TYPE)));
        assertThat(response, hasJsonPath("$.definition", equalTo(DEFINITION)));
        assertThat(response, hasJsonPath("$.applications[0].lifecycle_state", equalTo(ACTIVE.name())));
        assertThat(response, hasJsonPath("$.applications[0].api_ui", equalTo(API_UI)));
        assertThat(response, hasJsonPath("$.applications[0].api_url", equalTo(API_URL)));
        final String expectedUrl = localUriBuilder().path("/applications/" + APP1_NAME).toUriString();
        assertThat(response, hasJsonPath("$.applications[0].href", equalTo(expectedUrl)));
    }

    @Test
    public void shouldReturn404IfNoDefinitionFound() throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis/" + API_NAME + "/versions/" + API_VERSION_1 + "/definitions/XYZ", String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }
}
