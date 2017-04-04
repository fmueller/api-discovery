package org.zalando.apidiscovery.storage.api;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, String> {
    Optional<ApplicationEntity> findOneByName(String name);
}
