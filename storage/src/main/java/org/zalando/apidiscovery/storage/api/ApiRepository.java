package org.zalando.apidiscovery.storage.api;


import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiRepository extends JpaRepository<ApiEntity, Long> {

    //@Query(value = "SELECT new org.zalando.apidiscovery.storage.api.Api(a.api_name, ) FROM ApiEntity a GROUP BY a.api_name")
    //List<Api> findAllGroupedByApiName();

}
