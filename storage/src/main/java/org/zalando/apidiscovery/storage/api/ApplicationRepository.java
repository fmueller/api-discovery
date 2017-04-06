package org.zalando.apidiscovery.storage.api;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends CrudRepository<ApplicationEntity, String> {


    @Query("SELECT DISTINCT a FROM ApplicationEntity a " +
        "INNER JOIN a.apiDeploymentEntities dep " +
        "WHERE dep.api in (:apiEntities)")
    List<ApplicationEntity> findByApiIds(@Param("apiEntities") List<ApiEntity> apiEntities);

}
