package org.zalando.apidiscovery.storage.legacy;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

/**
 * This component schedules a recurring job which checks for lifecycle state changes:
 * <ul>
 * <li>if an API definition was not crawled successfully for certain amount of time mark it as inactive</li>
 * <li>if an API definition was not crawled successfully even longer mark it as decommissioned</li>
 * </ul>
 */
@Component
public class ApiLifecycleManager {

    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    public static final String DECOMMISSIONED = "DECOMMISSIONED";

    private final ApiDefinitionRepository repository;
    private final int markAsInactiveTime;
    private final int markAsDecommissionedTime;

    @Autowired
    ApiLifecycleManager(ApiDefinitionRepository repository,
                        @Value("${inactive.time}") int markAsInactiveTime,
                        @Value("${decommissioned.time}") int markAsDecommissionedTime) {
        this.repository = repository;
        this.markAsInactiveTime = markAsInactiveTime;
        this.markAsDecommissionedTime = markAsDecommissionedTime;
    }

    @Scheduled(fixedDelayString = "${lifecycle-check.delay}")
    public void checkLifecycleStates() {
        final OffsetDateTime now = now(UTC);
        inactivateApis(now);
        decomissionApis(now);
    }

    @Transactional
    public void inactivateApis(OffsetDateTime now) {
        final OffsetDateTime toOldApis = now.minusSeconds(markAsInactiveTime);

        List<ApiDefinition> inactivatedApis = repository.findOlderThanAndUnsuccessful(toOldApis);
        inactivatedApis.addAll(repository.findNotUpdatedSince(toOldApis));

        inactivatedApis.forEach(a -> a.setLifecycleState(INACTIVE));
        repository.save(inactivatedApis);
    }

    @Transactional
    public void decomissionApis(OffsetDateTime now) {
        List<ApiDefinition> decommissionedApis = repository.findNotUpdatedSinceAndInactive(now.minusSeconds(markAsDecommissionedTime));
        decommissionedApis.forEach(a -> a.setLifecycleState(DECOMMISSIONED));
        repository.save(decommissionedApis);
    }
}
