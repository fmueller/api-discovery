package org.zalando.apidiscovery.storage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.apidiscovery.storage.api.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.zalando.apidiscovery.storage.ApiLifecycleManager.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApiResourceIntegrationTest {

    private final static String TEST_API = "testAPI";
    private final static String ANOTHER_API = "anotherAPI";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApiRepository apiRepository;


    @Before
    public void cleanDatabase() {
        applicationRepository.deleteAll();
    }

    @Test
    public void shouldReturnAllApis() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(
            ApplicationEntity
                .builder()
                .name("testApp")
                .build());
        ApplicationEntity app2 = applicationRepository.save(
            ApplicationEntity
                .builder()
                .name("testApp2")
                .build());

        ApiEntity testAPi100 = ApiEntity.builder().apiName(TEST_API)
            .apiVersion("1.0.0")
                .build();
        ApiEntity testAPi101 = ApiEntity.builder().apiName(TEST_API)
            .apiVersion("1.0.1")
                .build();
        ApiEntity anotherAPi100 = ApiEntity.builder().apiName(ANOTHER_API)
                .apiVersion("1.0.0")
                .build();

        ApiDeploymentEntity testAPi100OnApp1 = ApiDeploymentEntity.builder()
            .api(testAPi100)
            .application(app1)
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .build();

        ApiDeploymentEntity testAPi101OnApp1 = ApiDeploymentEntity.builder()
            .api(testAPi101)
            .application(app1)
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .build();

        ApiDeploymentEntity anotherAPi100OnApp2 = ApiDeploymentEntity.builder()
            .api(anotherAPi100)
            .application(app2)
            .lifecycleState(ApiLifecycleState.INACTIVE)
            .build();

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi101.setApiDeploymentEntities(asList(testAPi101OnApp1));
        anotherAPi100.setApiDeploymentEntities(asList(anotherAPi100OnApp2));

        apiRepository.save(asList(testAPi100, testAPi101, anotherAPi100));

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
        ApplicationEntity app1 = applicationRepository.save(
            ApplicationEntity
                .builder()
                .name("testApp")
                .build());
        ApplicationEntity app2 = applicationRepository.save(
            ApplicationEntity
                .builder()
                .name("testApp2")
                .build());

        ApiEntity testAPi100 = ApiEntity.builder().apiName(TEST_API)
            .apiVersion("1.0.0")
            .build();
        ApiEntity anotherAPi100 = ApiEntity.builder().apiName(ANOTHER_API)
            .apiVersion("1.0.0")
            .build();

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
        ApplicationEntity app1 = applicationRepository.save(
            ApplicationEntity
                .builder()
                .name("testApp")
                .build());
        ApplicationEntity app2 = applicationRepository.save(
            ApplicationEntity
                .builder()
                .name("testApp2")
                .build());

        ApiEntity testAPi100 = ApiEntity.builder().apiName(TEST_API)
            .apiVersion("1.0.0")
            .build();
        ApiEntity testAPi101 = ApiEntity.builder().apiName(TEST_API)
            .apiVersion("1.0.1")
            .build();
        ApiEntity anotherAPi100 = ApiEntity.builder().apiName(ANOTHER_API)
            .apiVersion("1.0.0")
            .build();

        ApiDeploymentEntity testAPi100OnApp1 = ApiDeploymentEntity.builder()
            .api(testAPi100)
            .application(app1)
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .build();

        ApiDeploymentEntity testAPi101OnApp1 = ApiDeploymentEntity.builder()
            .api(testAPi101)
            .application(app1)
            .lifecycleState(ApiLifecycleState.INACTIVE)
            .build();

        ApiDeploymentEntity anotherAPi100OnApp2 = ApiDeploymentEntity.builder()
            .api(anotherAPi100)
            .application(app2)
            .lifecycleState(ApiLifecycleState.INACTIVE)
            .build();

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi101.setApiDeploymentEntities(asList(testAPi101OnApp1));
        anotherAPi100.setApiDeploymentEntities(asList(anotherAPi100OnApp2));

        apiRepository.save(asList(testAPi100, testAPi101, anotherAPi100));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis?lifecycle_state=INACTIVE", String.class);

        assertThat(responseEntity.getBody())
            .containsOnlyOnce(ANOTHER_API)
                .contains(INACTIVE)
                .doesNotContain(DECOMMISSIONED)
            .doesNotContain(TEST_API)
                .doesNotContain("\"" + ACTIVE + "\""); // necessary, otherwise INACTIVE would also match this;
    }


    @Test
    public void shouldReturnAllDecommissionedApis() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(
            ApplicationEntity
                .builder()
                .name("testApp")
                .build());
        ApplicationEntity app2 = applicationRepository.save(
            ApplicationEntity
                .builder()
                .name("testApp2")
                .build());

        ApiEntity testAPi100 = ApiEntity.builder().apiName(TEST_API)
            .apiVersion("1.0.0")
            .build();
        ApiEntity anotherAPi100 = ApiEntity.builder().apiName(ANOTHER_API)
            .apiVersion("1.0.0")
            .build();

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
                .doesNotContain("\"" + ACTIVE + "\""); // necessary, otherwise INACTIVE would also match this;
    }

}
