package org.zalando.apidiscovery.storage.repository;

import org.junit.Test;
import org.zalando.apidiscovery.storage.AbstractDatabaseComponentTest;
import org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState;

import java.util.List;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ApiRepositoryTest extends AbstractDatabaseComponentTest {

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
            .definitionHash("0")
            .created(now(UTC))
            .build();
        ApiEntity testAPi101 = ApiEntity.builder().apiName("testAPi")
            .apiVersion("1.0.1")
            .definitionHash("1")
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
            .definitionHash("f1")
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
