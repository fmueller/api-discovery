package org.zalando.apidiscovery.storage.api;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiRepository extends JpaRepository<ApiEntity, Long> {

    List<ApiEntity> findByApiName(String apiName);

    List<ApiEntity> findByApiNameAndApiVersion(String apiName, String apiVersion);

    List<ApiEntity> findByApiNameAndApiVersionAndDefinitionHash(String apiName, String apiVersion, String definitionHash);

}
