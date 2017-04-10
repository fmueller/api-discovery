package org.zalando.apidiscovery.storage;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.apidiscovery.storage.api.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.ApiEntity;
import org.zalando.apidiscovery.storage.api.ApiLifecycleState;
import org.zalando.apidiscovery.storage.api.ApiRepository;
import org.zalando.apidiscovery.storage.api.ApplicationEntity;
import org.zalando.apidiscovery.storage.api.ApplicationRepository;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiRepositoryTest {

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Before
    public void cleanDatabase() {
        applicationRepository.deleteAll();
        apiRepository.deleteAll();
    }

    @Test
    public void shouldReturnAllApis() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(
            ApplicationEntity
                .builder()
                .name("testApp")
                .created(now(UTC))
                .build());

        ApiEntity testAPi100 = ApiEntity.builder().apiName("testAPi")
            .apiVersion("1.0.0")
            .created(now(UTC))
            .build();
        ApiEntity testAPi101 = ApiEntity.builder().apiName("testAPi")
            .apiVersion("1.0.1")
            .created(now(UTC))
            .build();

        ApiDeploymentEntity apiDeploymentEntity1 = ApiDeploymentEntity.builder()
            .api(testAPi100)
            .application(app1)
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .created(now(UTC))
            .build();

        ApiDeploymentEntity apiDeploymentEntity2 = ApiDeploymentEntity.builder()
            .api(testAPi101)
            .application(app1)
            .created(now(UTC))
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .build();

        testAPi100.setApiDeploymentEntities(asList(apiDeploymentEntity1));
        testAPi101.setApiDeploymentEntities(asList(apiDeploymentEntity2));
        app1.setApiDeploymentEntities(asList(apiDeploymentEntity1, apiDeploymentEntity2));

        apiRepository.save(asList(testAPi100, testAPi101));

        List<ApiEntity> allApiEntitiesList = apiRepository.findAll();
        assertThat(allApiEntitiesList)
            .isNotNull()
            .hasSize(2);
    }

    @Test
    public void shouldPersistAndFetchOffsetDateCorrectly() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(
            ApplicationEntity
                .builder()
                .name("testApp")
                .created(now(UTC))
                .build());

        ApiEntity testAPi100 = ApiEntity.builder().apiName("testAPi")
            .apiVersion("1.0.0")
            .created(now(UTC))
            .build();


        ApiDeploymentEntity apiDeploymentEntity1 = ApiDeploymentEntity.builder()
            .api(testAPi100)
            .application(app1)
            .lifecycleState(ApiLifecycleState.ACTIVE)
            .created(now(UTC))
            .build();

        testAPi100.setApiDeploymentEntities(asList(apiDeploymentEntity1));
        app1.setApiDeploymentEntities(asList(apiDeploymentEntity1));

        apiRepository.save(asList(testAPi100));

        ApiEntity persistedApiEntity = apiRepository.findOne(testAPi100.getId());
        assertThat(persistedApiEntity.getCreated())
            .isEqualTo(testAPi100.getCreated());
    }
}
