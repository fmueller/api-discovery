package org.zalando.apidiscovery.storage.legacy;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ApiDefinitionRepository extends CrudRepository<ApiDefinition, String> {

    @Query("select a from ApiDefinition a where a.status = 'UNSUCCESSFUL' and (a.lastChanged is null or a.lastChanged < ?1)")
    List<ApiDefinition> findOlderThanAndUnsuccessful(OffsetDateTime olderThan);

    @Query("select a from ApiDefinition a where a.lastPersisted is null or a.lastPersisted < ?1")
    List<ApiDefinition> findNotUpdatedSince(OffsetDateTime olderThan);

    @Query("select a from ApiDefinition a where a.lifecycleState = 'INACTIVE' and (a.lastPersisted is null or a.lastPersisted < ?1)")
    List<ApiDefinition> findNotUpdatedSinceAndInactive(OffsetDateTime olderThan);

    List<ApiDefinition> findByLifecycleState(String lifecycleState);

    @Query("select new org.zalando.apidiscovery.storage.legacy.ApiDefinitionStatusStatistics(a.status, count(a)) from ApiDefinition a group by a.status")
    List<ApiDefinitionStatusStatistics> countStatus();

    @Query("select new org.zalando.apidiscovery.storage.legacy.ApiDefinitionStatusStatistics(a.lifecycleState, count(a)) from ApiDefinition a group by a.lifecycleState")
    List<ApiDefinitionStatusStatistics> countLifecycleStates();
}
