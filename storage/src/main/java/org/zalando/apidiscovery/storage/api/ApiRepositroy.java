package org.zalando.apidiscovery.storage.api;


import org.springframework.data.repository.CrudRepository;

public interface ApiRepositroy extends CrudRepository<ApiEntity, Long> {

}
