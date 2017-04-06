package org.zalando.apidiscovery.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.zalando.apidiscovery.storage.api.ApiEntityToVersionConverter.toVersionDtoList;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.ACTIVE;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.DECOMMISSIONED;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.INACTIVE;

@Service
public class ApiService {

    private final ApiRepository apiRepository;
    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApiService(ApiRepository apiRepository, ApplicationRepository applicationRepository) {
        this.apiRepository = apiRepository;
        this.applicationRepository = applicationRepository;
    }

    public List<ApiDto> getAllApis() {
        List<ApiEntity> apiEntities = apiRepository.findAll();

        return apiEntities
            .stream()
            .collect(groupingBy(ApiEntity::getApiName))
            .entrySet().stream()
            .map(entry -> new ApiDto(entry.getKey(), aggregateApplicationLifecycleStateForApi(entry.getValue())))
            .collect(toList());
    }

    public static ApiLifecycleState aggregateApplicationLifecycleStateForApi(List<ApiEntity> apiEntities) {
        List<ApiDeploymentEntity> apiDeploymentList = apiEntities.stream()
            .flatMap(apiEntity ->
                apiEntity.getApiDeploymentEntities() != null ? apiEntity.getApiDeploymentEntities().stream() : new ArrayList<ApiDeploymentEntity>().stream())
            .collect(toList());
        return aggregateApplicationLifecycleStateForDeploymentEntities(apiDeploymentList);
    }

    public static ApiLifecycleState aggregateApplicationLifecycleStateForDeploymentEntities(List<ApiDeploymentEntity> apiDeploymentEntities) {
        if (apiDeploymentEntities.stream()
            .filter(apiEntity -> ACTIVE.equals(apiEntity.getLifecycleState())).count() > 0) {
            return ACTIVE;
        } else if (apiDeploymentEntities.stream()
            .filter(apiEntity -> INACTIVE.equals(apiEntity.getLifecycleState())).count() > 0) {
            return INACTIVE;
        }
        return DECOMMISSIONED;
    }

    public List<ApiDto> getAllApis(ApiLifecycleState filterByLifecycleState) {
        return getAllApis().stream()
            .filter(api -> filterByLifecycleState.equals(api.getApiMetaData().getLifecycleState()))
            .collect(toList());
    }

    public Optional<ApiDto> getApi(String apiName) {
        List<ApiEntity> apiEntities = apiRepository.findByApiName(apiName);

        return apiEntities.stream()
            .findFirst()
            .map(apiEntity ->
                Optional.of(new ApiDto(apiEntity.getApiName(),
                    aggregateApplicationLifecycleStateForApi(apiEntities),
                    toVersionDtoList(apiEntities),
                    toApplicationDtoList(apiEntities))))
            .orElse(Optional.empty());
    }


    private List<ApplicationDto> toApplicationDtoList(List<ApiEntity> apiEntities) {
        return applicationRepository.findByApiIds(apiEntities).stream()
            .map(ApplicationEntityToApplicationDtoConverter::toApplicationDto)
            .collect(toList());
    }


}
