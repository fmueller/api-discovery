package org.zalando.apidiscovery.storage.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.zalando.apidiscovery.storage.TestDataHelper.readResource;

public class ApiDefinitionRestIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("classpath:uber.json")
    private Resource discoveredUberApiJson;


    @Value("classpath:invalid-crawler-data.json")
    private Resource invalidCrawledApi;

    @Value("classpath:minimal-crawler-data.json")
    private Resource minimalCrawledApi;

    @Test
    public void shouldCreateANewApplication() throws Exception {
        assertThat(applicationRepository.findOneByName("uber.api")).isNotPresent();

        restTemplate.exchange("/api-definitions", HttpMethod.POST, httpEntity(readResource(discoveredUberApiJson)), Void.class);

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

        restTemplate.exchange("/api-definitions", HttpMethod.POST, httpEntity(readResource(discoveredUberApiJson)), Void.class);
        final Optional<ApplicationEntity> appOption = applicationRepository.findOneByName("uber.api");
        assertThat(appOption).isPresent();
    }

    @Test
    public void shouldCreateANewApiVersion() throws Exception {
        assertThat(apiRepository.findAll().size()).isEqualTo(0);

        restTemplate.exchange("/api-definitions", HttpMethod.POST, httpEntity(readResource(discoveredUberApiJson)), Void.class);

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

        restTemplate.exchange("/api-definitions", HttpMethod.POST, httpEntity(readResource(minimalCrawledApi)), Void.class);

        final List<ApiEntity> allApis = apiRepository.findAll();
        assertThat(allApis.size()).isEqualTo(1);
        final ApiEntity api = allApis.get(0);
        assertThat(api.getDefinitionHash()).isEqualTo(definitionHash);
    }

    @Test
    public void shouldCreateANewApiDeployment() throws Exception {
        restTemplate.exchange("/api-definitions", HttpMethod.POST, httpEntity(readResource(discoveredUberApiJson)),
                Void.class);

        final List<ApiEntity> apis = apiRepository.findByApiNameAndApiVersion("uber-api", "1.0.0");
        assertThat(apis.size()).isEqualTo(1);

        final Optional<ApplicationEntity> app = applicationRepository.findOneByName("uber.api");
        assertThat(app).isPresent();

        final ApiDeploymentEntity deployment = entityManager
                .find(ApiDeploymentEntity.class, new ApiDeploymentEntity(apis.get(0), app.get()));
        assertThat(deployment.getLifecycleState()).isEqualTo(ApiLifecycleState.ACTIVE);
    }

    @Test
    public void shouldUseExistingApiDeploymentIfTheLinkAlreadyExists() throws Exception {
        restTemplate.exchange("/api-definitions", HttpMethod.POST, httpEntity(readResource(discoveredUberApiJson)), Void.class);
        restTemplate.exchange("/api-definitions", HttpMethod.POST, httpEntity(readResource(discoveredUberApiJson)), Void.class);

        final int deployments = entityManager
                .createNativeQuery("SELECT * FROM api_deployment;", ApiDeploymentEntity.class)
                .getResultList().size();

        assertThat(deployments).isEqualTo(1);
    }

    @Test
    public void shouldReturnBadRequestWhenProvidingNotParsableDefinition() throws Exception {
        final ResponseEntity<Void> response =
                restTemplate.exchange("/api-definitions", HttpMethod.POST, httpEntity(readResource(invalidCrawledApi)), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnALocationHeader() throws Exception {
        final ResponseEntity<Void> response =
                restTemplate.exchange("/api-definitions", HttpMethod.POST, httpEntity(readResource(minimalCrawledApi)), Void.class);
        final URI location = response.getHeaders().getLocation();
        final String uriPattern = "http://localhost(:\\d+)/apis/uber-api/versions/v1/definitions/\\d+";

        assertThat(location).isNotNull();
        assertThat(Pattern.matches(uriPattern, location.toString())).isTrue();
    }

    private HttpEntity<String> httpEntity(final String content) throws IOException, URISyntaxException {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", APPLICATION_JSON_UTF8_VALUE);
        return new HttpEntity<>(content, headers);
    }
}