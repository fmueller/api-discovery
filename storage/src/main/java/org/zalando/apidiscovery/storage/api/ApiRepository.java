package org.zalando.apidiscovery.storage.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApiRepository extends JpaRepository<ApiEntity, Long> {

    List<ApiEntity> findByApiName(String apiName);

    List<ApiEntity> findByApiNameAndApiVersion(String apiName, String apiVersion);

    List<ApiEntity> findByApiNameAndApiVersionAndDefinitionHash(String apiName, String apiVersion, String definitionHash);

    @Query("select coalesce(max(a.definitionId), 0) from ApiEntity a " +
            "where a.apiName = :apiName and a.apiVersion = :apiVersion")
    int getLastApiDefinitionId(@Param("apiName") String apiName, @Param("apiVersion") String apiVersion);

}
