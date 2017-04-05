package org.zalando.apidiscovery.storage.api;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiRepository extends JpaRepository<ApiEntity, Long> {

    List<ApiEntity> findByApiNameAndApiVersion(String apiName, String apiVersion);

    List<ApiEntity> findByApiNameAndApiVersionAndDefinition(String apiName, String apiVersion, String definition);

}
