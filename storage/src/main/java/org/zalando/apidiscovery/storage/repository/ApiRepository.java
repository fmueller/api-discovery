package org.zalando.apidiscovery.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApiRepository extends JpaRepository<ApiEntity, Long> {

    List<ApiEntity> findByApiName(String apiName);

    List<ApiEntity> findByApiNameAndApiVersionAndDefinitionHash(String apiName, String apiVersion, String definitionHash);

    Optional<ApiEntity> findByApiNameAndApiVersionAndDefinitionId(String apiName, String apiVersion, int definitionId);

    @Query("SELECT COALESCE(max(a.definitionId), 0) FROM ApiEntity a " +
            "WHERE a.apiName = :apiName AND a.apiVersion = :apiVersion")
    int getLastApiDefinitionId(@Param("apiName") String apiName, @Param("apiVersion") String apiVersion);

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
