package org.zalando.apidiscovery.storage.api;


import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiRepository extends JpaRepository<ApiEntity, Long> {

}
