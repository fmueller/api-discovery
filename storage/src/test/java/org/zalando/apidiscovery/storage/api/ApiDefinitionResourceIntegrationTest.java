package org.zalando.apidiscovery.storage.api;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.apidiscovery.storage.AbstractResourceIntegrationTest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.zalando.apidiscovery.storage.utils.DomainObjectGen.API_NAME;
import static org.zalando.apidiscovery.storage.utils.DomainObjectGen.API_UI;
import static org.zalando.apidiscovery.storage.utils.DomainObjectGen.API_URL;
import static org.zalando.apidiscovery.storage.utils.DomainObjectGen.API_VERSION_1;
import static org.zalando.apidiscovery.storage.utils.DomainObjectGen.API_VERSION_2;
import static org.zalando.apidiscovery.storage.utils.DomainObjectGen.APP1_NAME;
import static org.zalando.apidiscovery.storage.utils.DomainObjectGen.DEFINITION;
import static org.zalando.apidiscovery.storage.utils.DomainObjectGen.DEFINITION_TYPE;
import static org.zalando.apidiscovery.storage.utils.DomainObjectGen.givenApiDeployment;
import static org.zalando.apidiscovery.storage.utils.DomainObjectGen.givenApiEntity;
import static org.zalando.apidiscovery.storage.utils.DomainObjectGen.givenApplication;
import static org.zalando.apidiscovery.storage.TestDataHelper.readResource;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.ACTIVE;

public class ApiDefinitionResourceIntegrationTest extends AbstractResourceIntegrationTest {

    @Value("classpath:uber.json")
    private Resource discoveredUberApiJson;

    @Value("classpath:invalid-crawler-data.json")
    private Resource invalidCrawledApi;

    @Value("classpath:minimal-crawler-data.json")
    private Resource minimalCrawledApi;

    @Test
    public void shouldCreateANewApplication() throws Exception {
        assertThat(applicationRepository.findOneByName("uber.api")).isNotPresent();

        postApiDefinition(discoveredUberApiJson);

        final Optional<ApplicationEntity> appOption = applicationRepository.findOneByName("uber.api");
        assertThat(appOption).isPresent();
        final ApplicationEntity app = appOption.get();
        assertThat(app.getCreated()).isNotNull();
        assertThat(app.getAppUrl()).isEqualTo("uber.swagger.io");
    }

    @Test
    public void shouldLinkToExistingApplicationIfItAlreadyExists() throws Exception {
        final ApplicationEntity application = ApplicationEntity.builder()
            .name("uber.api")
            .created(now(UTC))
            .build();
        applicationRepository.saveAndFlush(application);

        postApiDefinition(discoveredUberApiJson);
        final Optional<ApplicationEntity> appOption = applicationRepository.findOneByName("uber.api");
        assertThat(appOption).isPresent();
    }

    @Test
    public void shouldCreateANewApiVersion() throws Exception {
        assertThat(apiRepository.findAll().size()).isEqualTo(0);

        postApiDefinition(discoveredUberApiJson);

        final List<ApiEntity> allApis = apiRepository.findAll();
        assertThat(allApis.size()).isEqualTo(1);
        final ApiEntity api = allApis.get(0);
        assertThat(api.getApiName()).isEqualTo("uber-api");
        assertThat(api.getApiVersion()).isEqualTo("1.0.0");
        assertThat(api.getCreated()).isNotNull();
        assertThat(api.getDefinitionType()).isEqualTo("swagger-2.0");
    }

    @Test
    public void shouldUseExistingApiVersionIfItAlreadyExists() throws Exception {
        assertThat(apiRepository.findAll().size()).isEqualTo(0);
        final String definitionHash = "cc9aa34e0c8343df59218a410e58a69a01a711d285ee0bd2ff5c4c8207a634e7";
        final ApiEntity apiEntity = ApiEntity.builder()
            .apiName("uber-api")
            .apiVersion("v1")
            .definition("{\"info\":{\"title\":\"Uber API\",\"version\":\"v1\"}}")
            .definitionHash(definitionHash)
            .build();
        apiRepository.saveAndFlush(apiEntity);

        postApiDefinition(minimalCrawledApi);

        final List<ApiEntity> allApis = apiRepository.findAll();
        assertThat(allApis.size()).isEqualTo(1);
        final ApiEntity api = allApis.get(0);
        assertThat(api.getDefinitionHash()).isEqualTo(definitionHash);
    }

    @Test
    public void shouldCreateANewApiDeployment() throws Exception {
        postApiDefinition(discoveredUberApiJson);

        final List<ApiEntity> apis = apiRepository.findByApiNameAndApiVersion("uber-api", "1.0.0");
        assertThat(apis.size()).isEqualTo(1);

        final Optional<ApplicationEntity> app = applicationRepository.findOneByName("uber.api");
        assertThat(app).isPresent();

        final ApiDeploymentEntity deployment = entityManager
            .find(ApiDeploymentEntity.class, new ApiDeploymentEntity(apis.get(0), app.get()));
        assertThat(deployment.getLifecycleState()).isEqualTo(ACTIVE);
    }

    @Test
    public void shouldUseExistingApiDeploymentIfTheLinkAlreadyExists() throws Exception {
        postApiDefinition(discoveredUberApiJson);
        postApiDefinition(discoveredUberApiJson);

        final int deployments = entityManager
            .createNativeQuery("SELECT * FROM api_deployment;", ApiDeploymentEntity.class)
            .getResultList().size();

        assertThat(deployments).isEqualTo(1);
    }

    @Test
    public void shouldReturnBadRequestWhenProvidingNotParsableDefinition() throws Exception {
        final ResponseEntity<Void> response = postApiDefinition(invalidCrawledApi);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnALocationHeader() throws Exception {
        final ResponseEntity<Void> response = postApiDefinition(minimalCrawledApi);
        final URI location = response.getHeaders().getLocation();
        final String uriPattern = "http://localhost(:\\d+)/apis/uber-api/versions/v1/definitions/\\d+";

        assertThat(location).isNotNull();
        assertThat(Pattern.matches(uriPattern, location.toString())).isTrue();
    }

    @Test
    public void shouldReturnOneSpecificApiDefinition() {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        testAPi100.setDefinition("definition_version_1");
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_2, 1);
        testAPi200.setDefinition("definition_version_2");

        testAPi100.setApiDeploymentEntities(asList(givenApiDeployment(testAPi100, app1)));
        testAPi200.setApiDeploymentEntities(asList(givenApiDeployment(testAPi200, app1)));

        apiRepository.save(asList(testAPi100, testAPi200));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis/" + API_NAME + "/versions/" + API_VERSION_1 + "/definitions/1", String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        final String response = responseEntity.getBody();
        MatcherAssert.assertThat(response, hasJsonPath("$.definition", equalTo("definition_version_1")));
    }

    @Test
    public void shouldReturnOneApiDefinition() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));

        apiRepository.save(asList(testAPi100));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis/" + API_NAME + "/versions/" + API_VERSION_1 + "/definitions/" + testAPi100.getDefinitionId(), String.class);

        final String response = responseEntity.getBody();
        MatcherAssert.assertThat(response, hasNoJsonPath("$.id"));
        MatcherAssert.assertThat(response, hasJsonPath("$.type", Matchers.equalTo(DEFINITION_TYPE)));
        MatcherAssert.assertThat(response, hasJsonPath("$.definition", Matchers.equalTo(DEFINITION)));
        MatcherAssert.assertThat(response, hasJsonPath("$.applications[0].lifecycle_state", Matchers.equalTo(ACTIVE.name())));
        MatcherAssert.assertThat(response, hasJsonPath("$.applications[0].api_ui", Matchers.equalTo(API_UI)));
        MatcherAssert.assertThat(response, hasJsonPath("$.applications[0].api_url", Matchers.equalTo(API_URL)));
        final String expectedUrl = localUriBuilder().path("/applications/" + APP1_NAME).toUriString();
        MatcherAssert.assertThat(response, hasJsonPath("$.applications[0].href", Matchers.equalTo(expectedUrl)));
    }

    @Test
    public void shouldReturn404IfNoDefinitionFound() throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
            "/apis/" + API_NAME + "/versions/" + API_VERSION_1 + "/definitions/XYZ", String.class);

        MatcherAssert.assertThat(responseEntity.getStatusCode(), Matchers.equalTo(HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<Void> postApiDefinition(Resource apiDefinition) throws IOException, URISyntaxException {
        return restTemplate.exchange("/api-definitions", HttpMethod.POST, httpEntity(readResource(apiDefinition)), Void.class);
    }

    private HttpEntity<String> httpEntity(final String content) throws IOException, URISyntaxException {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", APPLICATION_JSON_UTF8_VALUE);
        return new HttpEntity<>(content, headers);
    }
}
