package org.zalando.apidiscovery.storage.api;

import org.springframework.data.repository.CrudRepository;

public interface ApplicationRepository extends CrudRepository<ApplicationEntity, String> {
}
