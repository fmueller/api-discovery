package org.zalando.apidiscovery.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.apidiscovery.storage.api.*;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.zalando.apidiscovery.storage.ApiLifecycleManager.ACTIVE;
import static org.zalando.apidiscovery.storage.ApiLifecycleManager.INACTIVE;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApiResourceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    public void shouldReturnAllApis() throws Exception {
        ApplicationEntity app1 = ApplicationEntity.builder().name("testApp")
                .lastCrawled(now())
                .created(now())
                .crawledState("SUCCESSFUL")
                .build();
        ApplicationEntity app2 = ApplicationEntity.builder().name("testApp2")
                .lastCrawled(now())
                .created(now())
                .crawledState("SUCCESSFUL")
                .build();

        ApiEntity testAPi100 = ApiEntity.builder().apiName("testAPi")
                .apiVersion("1.0.0")
                .lifecycleState(ApiLifecycleState.INACTIVE)
                .application(app1)
                .build();
        ApiEntity testAPi101 = ApiEntity.builder().apiName("testAPi")
                .apiVersion("1.0.1")
                .lifecycleState(ApiLifecycleState.ACTIVE)
                .application(app1)
                .build();
        ApiEntity anotherAPi101 = ApiEntity.builder().apiName("anotherApi")
                .apiVersion("1.0.0")
                .lifecycleState(ApiLifecycleState.INACTIVE)
                .application(app2)
                .build();

        app1.setApiEntities(asList(testAPi100, testAPi101));
        app2.setApiEntities(asList(anotherAPi101));

        applicationRepository.save(asList(app1, app2));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis", String.class);

        assertThat(responseEntity.getBody())
                .containsOnlyOnce("testAPi")
                .containsOnlyOnce("anotherApi")
                .contains(ACTIVE)
                .contains(INACTIVE);
    }

    @Test
    public void shouldReturnAllActiveApis() throws Exception {
        ApplicationEntity app1 = ApplicationEntity.builder().name("testApp")
                .lastCrawled(now())
                .created(now())
                .crawledState("SUCCESSFUL")
                .build();
        ApplicationEntity app2 = ApplicationEntity.builder().name("testApp2")
                .lastCrawled(now())
                .created(now())
                .crawledState("SUCCESSFUL")
                .build();

        ApiEntity testAPi100 = ApiEntity.builder().apiName("testAPi")
                .apiVersion("1.0.0")
                .lifecycleState(ApiLifecycleState.ACTIVE)
                .application(app1)
                .build();
        ApiEntity anotherAPi101 = ApiEntity.builder().apiName("anotherApi")
                .apiVersion("1.0.0")
                .lifecycleState(ApiLifecycleState.INACTIVE)
                .application(app2)
                .build();

        app1.setApiEntities(asList(testAPi100));
        app2.setApiEntities(asList(anotherAPi101));

        applicationRepository.save(asList(app1, app2));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis?lifecycle_state=ACTIVE", String.class);

        assertThat(responseEntity.getBody())
                .containsOnlyOnce("testAPi")
                .contains(ACTIVE)
                .doesNotContain(INACTIVE);
    }

    @Test
    public void shouldReturnAllInActiveApis() throws Exception {
        ApplicationEntity app1 = ApplicationEntity.builder().name("testApp")
                .lastCrawled(now())
                .created(now())
                .crawledState("SUCCESSFUL")
                .build();
        ApplicationEntity app2 = ApplicationEntity.builder().name("testApp2")
                .lastCrawled(now())
                .created(now())
                .crawledState("SUCCESSFUL")
                .build();

        ApiEntity testAPi100 = ApiEntity.builder().apiName("testAPi")
                .apiVersion("1.0.0")
                .lifecycleState(ApiLifecycleState.ACTIVE)
                .application(app1)
                .build();
        ApiEntity anotherAPi101 = ApiEntity.builder().apiName("anotherApi")
                .apiVersion("1.0.0")
                .lifecycleState(ApiLifecycleState.INACTIVE)
                .application(app2)
                .build();

        app1.setApiEntities(asList(testAPi100));
        app2.setApiEntities(asList(anotherAPi101));

        applicationRepository.save(asList(app1, app2));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/apis?lifecycle_state=INACTIVE", String.class);

        assertThat(responseEntity.getBody())
                .containsOnlyOnce("anotherApi")
                .contains(INACTIVE)
                .doesNotContain("\"" + ApiLifecycleManager.ACTIVE + "\""); // necessary, otherwise INACTIVE would also match this;
    }

}
