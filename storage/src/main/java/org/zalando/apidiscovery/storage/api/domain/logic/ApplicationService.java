package org.zalando.apidiscovery.storage.api.domain.logic;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.apidiscovery.storage.api.domain.model.Application;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;
import org.zalando.apidiscovery.storage.api.repository.ApplicationEntity;
import org.zalando.apidiscovery.storage.api.repository.ApplicationRepository;

import static java.util.stream.Collectors.toList;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<Application> getAllApplications() {
        try (Stream<ApplicationEntity> applicationEntityList = applicationRepository.findAll()) {
            return applicationEntityList
                .map(ApplicationEntityToApplicationDtoConverter::toApplication)
                .collect(toList());
        }
    }

    public Optional<Application> getApplication(String applicationName) {
        return applicationRepository.findOne(applicationName)
            .map(ApplicationEntityToApplicationDtoConverter::toApplication);
    }

    public List<Application> getApplicationsByApiEntities(List<ApiEntity> apiEntities) {
        return applicationRepository.findByApiIds(apiEntities).stream()
            .map(ApplicationEntityToApplicationDtoConverter::toApplication)
            .collect(toList());
    }
}
