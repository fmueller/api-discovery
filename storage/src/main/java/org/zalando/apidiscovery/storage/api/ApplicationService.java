package org.zalando.apidiscovery.storage.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<ApplicationDto> getAllApplications() {
        try (Stream<ApplicationEntity> applicationEntityList = applicationRepository.findAll()) {
            return applicationEntityList
                .map(ApplicationEntityToApplicationDtoConverter::toApplicationDto)
                .collect(toList());
        }
    }

    public Optional<ApplicationDto> getApplication(String applicationName) {
        return applicationRepository.findOne(applicationName)
            .map(ApplicationEntityToApplicationDtoConverter::toApplicationDto);
    }

    public List<ApplicationDto> getApplicationsByApiEntities(List<ApiEntity> apiEntities) {
        return applicationRepository.findByApiIds(apiEntities).stream()
            .map(ApplicationEntityToApplicationDtoConverter::toApplicationDto)
            .collect(toList());
    }
}
