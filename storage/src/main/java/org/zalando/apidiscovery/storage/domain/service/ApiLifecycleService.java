package org.zalando.apidiscovery.storage.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState;
import org.zalando.apidiscovery.storage.repository.ApiEntity;
import org.zalando.apidiscovery.storage.repository.ApiRepository;

import java.time.OffsetDateTime;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

/**
 * This component schedules a recurring job which checks for lifecycle state changes:
 * <ul>
 * <li>if an API deployment was not crawled successfully for certain amount of time mark it as inactive</li>
 * <li>if an API deployment was not crawled successfully even longer mark it as decommissioned</li>
 * </ul>
 */
@Component
@Slf4j
class ApiLifecycleService {

    private final ApiRepository apiRepository;
    private final int markAsInactiveTime;
    private final int markAsDecommissionedTime;

    @Autowired
    ApiLifecycleService(ApiRepository apiRepository,
                        @Value("${inactive.time}") int markAsInactiveTime,
                        @Value("${decommissioned.time}") int markAsDecommissionedTime) {
        this.apiRepository = apiRepository;
        this.markAsInactiveTime = markAsInactiveTime;
        this.markAsDecommissionedTime = markAsDecommissionedTime;
    }

    @Scheduled(fixedDelayString = "${lifecycle-check.delay}")
    void checkLifecycleStates() {
        final OffsetDateTime now = now(UTC);
        inactivateApis(now);
        decommissionApis(now);
    }

    /**
     * protected for testing purpose
     */
    protected void inactivateApis(OffsetDateTime now) {
        final OffsetDateTime tooOldApis = now.minusSeconds(markAsInactiveTime);

        List<ApiEntity> inactivatedApis = apiRepository.findNotUpdatedSinceAndActive(tooOldApis);
        inactivatedApis.forEach(a -> a.getApiDeploymentEntities().stream()
            .filter(d -> d.getLifecycleState() == ApiLifecycleState.ACTIVE
                && (d.getLastCrawled() == null || d.getLastCrawled().isBefore(tooOldApis)))
            .forEach(d -> d.setLifecycleState(ApiLifecycleState.INACTIVE)));

        log.info("Marked {} api deployments as inactive", inactivatedApis.size());
        apiRepository.save(inactivatedApis);
    }

    /**
     * protected for testing purpose
     */
    protected void decommissionApis(OffsetDateTime now) {
        final OffsetDateTime tooOldApis = now.minusSeconds(markAsDecommissionedTime);

        List<ApiEntity> decommissionedApis = apiRepository.findNotUpdatedSinceAndInactive(tooOldApis);
        decommissionedApis.forEach(a -> a.getApiDeploymentEntities().stream()
            .filter(d -> d.getLifecycleState() == ApiLifecycleState.INACTIVE
                && (d.getLastCrawled() == null || d.getLastCrawled().isBefore(tooOldApis)))
            .forEach(d -> d.setLifecycleState(ApiLifecycleState.DECOMMISSIONED)));

        log.info("Marked {} api deployments as decommissioned", decommissionedApis.size());
        apiRepository.save(decommissionedApis);
    }
}
