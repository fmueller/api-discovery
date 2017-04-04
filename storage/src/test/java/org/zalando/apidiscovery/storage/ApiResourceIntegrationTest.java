package org.zalando.apidiscovery.storage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.apidiscovery.storage.api.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.ApiEntity;
import org.zalando.apidiscovery.storage.api.ApiLifecycleState;
import org.zalando.apidiscovery.storage.api.ApiRepository;
import org.zalando.apidiscovery.storage.api.ApplicationEntity;
import org.zalando.apidiscovery.storage.api.ApplicationRepository;

import static java.lang.String.valueOf;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.zalando.apidiscovery.storage.ApiLifecycleManager.ACTIVE;
import static org.zalando.apidiscovery.storage.ApiLifecycleManager.DECOMMISSIONED;
import static org.zalando.apidiscovery.storage.ApiLifecycleManager.INACTIVE;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApiResourceIntegrationTest {

    private final static String TEST_API = "testAPI";
    private final static String ANOTHER_API = "anotherAPI";
    private final static String V1 = "1.0.0";
    private final static String V2 = "2.0.0";
    private final static String APP1 = "app1";
    private final static String APP2 = "app2";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApiRepository apiRepository;

    @LocalServerPort
    private int port;


    @Before
    public void cleanDatabase() {
        applicationRepository.deleteAll();
        apiRepository.deleteAll();
    }

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

        assertThat(responseEntity.getBody())
            .containsOnlyOnce(TEST_API)
            .containsOnlyOnce(ANOTHER_API)
            .contains(ACTIVE)
            .contains(INACTIVE);
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

        assertThat(responseEntity.getBody())
            .containsOnlyOnce(TEST_API)
            .contains(ACTIVE)
            .doesNotContain(ANOTHER_API)
            .doesNotContain(INACTIVE);
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

        assertThat(responseEntity.getBody())
            .containsOnlyOnce(ANOTHER_API)
            .contains(INACTIVE)
            .doesNotContain(DECOMMISSIONED)
            .doesNotContain(TEST_API)
            .doesNotContain(exact(ACTIVE));
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

        assertThat(responseEntity.getBody())
            .containsOnlyOnce(ANOTHER_API)
            .contains(DECOMMISSIONED)
            .doesNotContain(exact(ACTIVE));
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

        assertThat(responseEntity.getStatusCode())
            .isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody())
            .containsOnlyOnce(exact(TEST_API))
            .contains(exact(ACTIVE))
            .containsOnlyOnce(exact(V1))
            .containsOnlyOnce(exact(V1))
            .containsOnlyOnce(exact(APP1))
            .containsOnlyOnce(exact(APP2))
            .contains(localUriBuilder()
                .path("applications/app1")
                .toUriString())
            .contains(localUriBuilder()
                .path("apis/" + TEST_API + "/versions/1.0.0/definitions/" + valueOf(testAPi100.getId()))
                .toUriString());
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
        assertThat(responseEntity.getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
    }

    private UriComponentsBuilder localUriBuilder() {
        return UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(port);
    }

    private static String exact(String field) {
        return "\"" + field + "\"";
    }
}
