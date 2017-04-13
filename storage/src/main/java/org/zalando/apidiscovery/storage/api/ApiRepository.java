package org.zalando.apidiscovery.storage.api;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ApiRepository extends JpaRepository<ApiEntity, Long> {

    List<ApiEntity> findByApiName(String apiName);

    @Query("SELECT a FROM ApiEntity a " +
        "INNER JOIN a.apiDeploymentEntities dep " +
        "WHERE dep.lifecycleState = 'ACTIVE' AND a.apiName = ?1")
    List<ApiEntity> findByApiNameAndLifecycleStateIsActive(String apiName);

    @Query("SELECT a FROM ApiEntity a " +
        "INNER JOIN a.apiDeploymentEntities dep " +
        "WHERE dep.lifecycleState = 'INACTIVE' AND a.apiName = ?1 " +
        "AND NOT exists (SELECT 1 FROM ApiDeploymentEntity d WHERE d.lifecycleState = 'ACTIVE' AND d.api.apiName = ?1)")
    List<ApiEntity> findByApiNameAndLifecycleStateIsInactive(String apiName);

    @Query("SELECT a FROM ApiEntity a " +
        "INNER JOIN a.apiDeploymentEntities dep " +
        "WHERE dep.lifecycleState = 'DECOMMISSIONED' AND a.apiName = ?1 " +
        "AND NOT exists (SELECT 1 FROM ApiDeploymentEntity d " +
        "WHERE (d.lifecycleState = 'ACTIVE' OR d.lifecycleState = 'INACTIVE') AND d.api.apiName = ?1)")
    List<ApiEntity> findByApiNameAndLifecycleStateIsDecommissioned(String apiName);


    List<ApiEntity> findByApiNameAndApiVersion(String apiName, String version);

}
