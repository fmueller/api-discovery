package org.zalando.apidiscovery.storage.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, String> {

    @Query("SELECT DISTINCT a FROM ApplicationEntity a " +
            "INNER JOIN a.apiDeploymentEntities dep " +
            "WHERE dep.api in (:apiEntities)")
    List<ApplicationEntity> findByApiIds(@Param("apiEntities") List<ApiEntity> apiEntities);

    Optional<ApplicationEntity> findOneByName(String name);

}
