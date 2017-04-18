package org.zalando.apidiscovery.storage.api;


import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends Repository<ApplicationEntity, String> {


    @Query("SELECT DISTINCT a FROM ApplicationEntity a " +
            "INNER JOIN a.apiDeploymentEntities dep " +
            "WHERE dep.api in (:apiEntities)")
    List<ApplicationEntity> findByApiIds(@Param("apiEntities") List<ApiEntity> apiEntities);

    Optional<ApplicationEntity> findOne(String name);

    void deleteAll();

    Stream<ApplicationEntity> findAll();

    ApplicationEntity save(ApplicationEntity applicationEntity);

    ApplicationEntity saveAndFlush(ApplicationEntity applicationEntity);

    Optional<ApplicationEntity> findOneByName(String name);

}
