package org.zalando.apidiscovery.storage.api;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.apidiscovery.storage.api.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;
import org.zalando.apidiscovery.storage.api.repository.ApplicationEntity;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.API_NAME;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.API_UI;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.API_URL;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.API_VERSION_1;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.API_VERSION_2;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.APP1_NAME;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.DEFINITION;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.DEFINITION_TYPE;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.givenApiDeployment;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.givenApiEntity;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.givenApplication;
import static org.zalando.apidiscovery.storage.api.domain.model.ApiLifecycleState.ACTIVE;
import static org.zalando.apidiscovery.storage.api.domain.model.ApiLifecycleState.DECOMMISSIONED;
import static org.zalando.apidiscovery.storage.api.domain.model.ApiLifecycleState.INACTIVE;

public class ApiVersionResourceIntegrationTest extends AbstractResourceIntegrationTest {

    @Test
    public void shouldGroupVersionOfGivenApi() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_1, 2);
        testAPi200.setDefinition("definition2");

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1, INACTIVE);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis/" + API_NAME + "/versions", String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.versions", hasSize(1)));
        assertThat(response, hasJsonPath("$.versions..definition", hasItems(DEFINITION, "definition2")));
        final String expectedUrl = localUriBuilder().path("/applications/" + APP1_NAME).toUriString();
        assertThat(response, hasJsonPath("$.versions..href", hasItem(expectedUrl)));
        assertThat(response, hasJsonPath("$.versions[0].lifecycle_state", equalTo(ACTIVE.name())));
        assertThat(response, hasJsonPath("$..applications..lifecycle_state", hasItems(ACTIVE.name(), INACTIVE.name())));
    }

    @Test
    public void shouldGroupVersionsOfGivenApi() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_2, 1);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1, INACTIVE);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis/" + API_NAME + "/versions", String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.versions", hasSize(2)));
        assertThat(response, hasJsonPath("$.versions..api_version", hasItems(API_VERSION_1, API_VERSION_2)));
        final String expectedUrl = localUriBuilder().path("/applications/" + APP1_NAME).toUriString();
        assertThat(response, hasJsonPath("$.versions..href", hasItem(expectedUrl)));
        assertThat(response, hasJsonPath("$.versions..lifecycle_state", hasItems(ACTIVE.name(), INACTIVE.name())));
    }


    @Test
    public void shouldReturnAllActiveVersions() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_2, 1);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1, INACTIVE);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis/" + API_NAME + "/versions?lifecycle_state=ACTIVE", String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.versions", hasSize(1)));
        assertThat(response, hasJsonPath("$.versions[0].lifecycle_state", equalTo(ACTIVE.name())));
    }

    @Test
    public void shouldReturnAllInactiveVersions() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_2, 1);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1, INACTIVE);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1, DECOMMISSIONED);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis/" + API_NAME + "/versions?lifecycle_state=INACTIVE", String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.versions", hasSize(1)));
        assertThat(response, hasJsonPath("$.versions[0].lifecycle_state", equalTo(INACTIVE.name())));
    }

    @Test
    public void shouldNotFindInactiveVersion() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi100_1 = givenApiEntity(API_NAME, API_VERSION_1, 2);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1, INACTIVE);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi100_1, app1, ACTIVE);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi100_1.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi100_1));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis/" + API_NAME + "/versions?lifecycle_state=INACTIVE", String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.versions", hasSize(0)));
    }

    @Test
    public void shouldReturnAllDecommissionedVersions() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_2, 2);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1, DECOMMISSIONED);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1, DECOMMISSIONED);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis/" + API_NAME + "/versions?lifecycle_state=DECOMMISSIONED", String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.versions", hasSize(2)));
        assertThat(response, hasJsonPath("$.versions[0].lifecycle_state", equalTo(DECOMMISSIONED.name())));
    }

    @Test
    public void shouldNotFindDecommissionedVersion() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi100_1 = givenApiEntity(API_NAME, API_VERSION_1, 2);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1, DECOMMISSIONED);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi100_1, app1, ACTIVE);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi100_1.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi100_1));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis/" + API_NAME + "/versions?lifecycle_state=DECOMMISSIONED", String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.versions", hasSize(0)));
    }

    @Test
    public void shouldReturnStatusCode400ForUnknownApiLifecycleState() throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis/" + API_NAME + "/versions?lifecycle_state=UNKNOWN", String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void shouldReturnOneVersion() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_2, 1);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis/" + API_NAME + "/versions/" + API_VERSION_1, String.class);

        final String response = responseEntity.getBody();
        assertThat(response, hasJsonPath("$.api_version", equalTo(API_VERSION_1)));
        assertThat(response, hasJsonPath("$.lifecycle_state", equalTo(ACTIVE.name())));
        assertThat(response, hasJsonPath("$.definitions[0].type", equalTo(DEFINITION_TYPE)));
        assertThat(response, hasJsonPath("$.definitions[0].definition", equalTo(DEFINITION)));
        assertThat(response, hasJsonPath("$.definitions[0].applications[0].lifecycle_state", equalTo(ACTIVE.name())));
        assertThat(response, hasJsonPath("$.definitions[0].applications[0].api_ui", equalTo(API_UI)));
        assertThat(response, hasJsonPath("$.definitions[0].applications[0].api_url", equalTo(API_URL)));
        //assertThat(response, hasJsonPath("$.definitions[0].applications[0].last_updated", equalTo(NOW.toString())));
        //assertThat(response, hasJsonPath("$.definitions[0].applications[0].created", equalTo(NOW.toString())));
        final String expectedUrl = localUriBuilder().path("/applications/" + APP1_NAME).toUriString();
        assertThat(response, hasJsonPath("$.definitions[0].applications[0].href", equalTo(expectedUrl)));
    }

    @Test
    public void shouldReturn404IfNoVersionFound() throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis/" + API_NAME + "/versions/" + API_VERSION_1, String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

}
