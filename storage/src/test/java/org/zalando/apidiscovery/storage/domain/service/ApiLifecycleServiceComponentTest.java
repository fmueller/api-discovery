package org.zalando.apidiscovery.storage.domain.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.apidiscovery.storage.AbstractServiceComponentTest;
import org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState;
import org.zalando.apidiscovery.storage.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.repository.ApiEntity;
import org.zalando.apidiscovery.storage.repository.ApplicationEntity;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class ApiLifecycleServiceComponentTest extends AbstractServiceComponentTest {

    @Autowired
    private ApiLifecycleService lifecycleService;

    @Before
    public void setUp() {
        lifecycleService = new ApiLifecycleService(apiRepository, 1, 1);
    }

    @Test
    public void shouldMarkApiDeploymentsInactiveWhenTheyAreTooOld() {
        final OffsetDateTime now = now(UTC);

        createActiveApis(5, now.minusHours(1));
        assertThat(apiRepository.findNotUpdatedSinceAndActive(now)).hasSize(5);

        lifecycleService.inactivateApis(now);

        assertThat(apiRepository.findNotUpdatedSinceAndActive(now)).isEmpty();
    }

    @Test
    public void shouldMarkApiDeploymentsDecommissionedWhenTheyAreTooOld() {
        final OffsetDateTime now = now(UTC);

        createActiveApis(5, now.minusHours(1));
        createInactiveApis(3, now.minusHours(1));

        assertThat(apiRepository.findDecommissionedApis()).isEmpty();
        assertThat(apiRepository.findNotUpdatedSinceAndActive(now)).hasSize(5);
        assertThat(apiRepository.findNotUpdatedSinceAndInactive(now)).hasSize(3);

        lifecycleService.decommissionApis(now);

        assertThat(apiRepository.findNotUpdatedSinceAndActive(now)).hasSize(5);
        assertThat(apiRepository.findNotUpdatedSinceAndInactive(now)).isEmpty();
        assertThat(apiRepository.findDecommissionedApis()).hasSize(3);
    }

    private void createActiveApis(int numberOfApis, OffsetDateTime now) {
        createApisWithLifecycleState(numberOfApis, now, ApiLifecycleState.ACTIVE);
    }

    private void createInactiveApis(int numberOfApis, OffsetDateTime now) {
        createApisWithLifecycleState(numberOfApis, now, ApiLifecycleState.INACTIVE);
    }

    private void createApisWithLifecycleState(int numberOfApis, OffsetDateTime now, ApiLifecycleState lifecycleState) {
        for (int i = 0; i < numberOfApis; i++) {
            ApplicationEntity application = applicationRepository.save(
                ApplicationEntity
                    .builder()
                    .name(RandomStringUtils.randomAlphabetic(10))
                    .created(now)
                    .build());

            ApiEntity api = ApiEntity.builder().apiName(RandomStringUtils.randomAlphabetic(20))
                .apiVersion("1.0.0")
                .definitionHash("0")
                .created(now)
                .build();

            ApiDeploymentEntity apiDeploymentEntity = ApiDeploymentEntity.builder()
                .api(api)
                .application(application)
                .lifecycleState(lifecycleState)
                .lastCrawled(now)
                .created(now)
                .build();

            List<ApiDeploymentEntity> deployments = new LinkedList<>();
            deployments.add(apiDeploymentEntity);
            api.setApiDeploymentEntities(deployments);
            apiRepository.save(api);
        }
    }
}
