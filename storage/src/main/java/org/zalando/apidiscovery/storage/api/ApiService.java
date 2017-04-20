package org.zalando.apidiscovery.storage.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.apidiscovery.storage.api.DeploymentDto.ApplicationDto;
import org.zalando.apidiscovery.storage.api.DeploymentDto.DefinitionDto;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.zalando.apidiscovery.storage.api.ApiEntityToVersionConverter.toVersionDtoList;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.ACTIVE;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.DECOMMISSIONED;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.INACTIVE;

@Service
public class ApiService {

    private final ApplicationService applicationService;
    private final ApiRepository apiRepository;

    @Autowired
    public ApiService(ApiRepository apiRepository, ApplicationService applicationService) {
        this.apiRepository = apiRepository;
        this.applicationService = applicationService;
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

        if (!apiEntities.isEmpty()) {
            ApiLifecycleState lifecycleState = aggregateApplicationLifecycleStateForApi(apiEntities);
            List<VersionsDto> versions = toVersionDtoList(apiEntities);
            List<org.zalando.apidiscovery.storage.api.ApplicationDto> applications = applicationService.getApplicationsByApiEntities(apiEntities);

            return Optional.of(new ApiDto(apiName,
                lifecycleState,
                versions,
                applications));
        }
        return Optional.empty();
    }

    public List<VersionsDto> getVersionsForApi(String apiId) {
        List<ApiEntity> apiEntities = apiRepository.findByApiName(apiId);
        return apiEntities.isEmpty() ? Collections.emptyList() : toVersionDtoList(apiEntities);
    }

    public List<VersionsDto> getVersionsForApi(String apiId, ApiLifecycleState lifecycleState) {
        List<ApiEntity> apiEntities = getApiEntityByLifecycleState(apiId, lifecycleState);
        return apiEntities.isEmpty() ? Collections.emptyList() : toVersionDtoList(apiEntities);
    }

    private List<ApiEntity> getApiEntityByLifecycleState(String apiId, ApiLifecycleState lifecycleState) {
        switch (lifecycleState) {
            case ACTIVE:
                return apiRepository.findByApiNameAndLifecycleStateIsActive(apiId);
            case INACTIVE:
                return apiRepository.findByApiNameAndLifecycleStateIsInactive(apiId);
            case DECOMMISSIONED:
                return apiRepository.findByApiNameAndLifecycleStateIsDecommissioned(apiId);
            default:
                throw new UnsupportedOperationException(format("ApiLifecycleState [{0}] not supported!", lifecycleState));
        }
    }

    public Optional<VersionsDto> getVersion(String apiId, String version) {
        List<ApiEntity> apiEntities = apiRepository.findByApiNameAndApiVersion(apiId, version);
        return toVersionDtoList(apiEntities).stream()
            .findAny();
    }

    public Optional<ApiDefinitionDto> getApiDefinitionDto(String definitionId) {
        try {
            Long id = Long.valueOf(definitionId);
            return Optional.ofNullable(apiRepository.findOne(id))
                .map(ApiEntityToApiDefinitionConverter::toApiDefinitionDto);
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }

    public List<DeploymentDto> getDeploymentsForApi(String apiId) {
        return apiRepository.findByApiName(apiId).stream()
            .flatMap(apiEntity -> apiEntityToDeploymentDtoList(apiEntity).stream())
            .collect(toList());
    }

    private List<DeploymentDto> apiEntityToDeploymentDtoList(ApiEntity apiEntity) {
        return apiEntity.getApiDeploymentEntities().stream()
            .map(apiDeploymentEntity -> apiDeploymentToDeploymentDto(apiDeploymentEntity))
            .collect(toList());
    }

    private DeploymentDto apiDeploymentToDeploymentDto(ApiDeploymentEntity apiDeploymentEntity) {
        return DeploymentDto.builder()
            .apiVersion(apiDeploymentEntity.getApi().getApiVersion())
            .application(new ApplicationDto(apiDeploymentEntity.getApplication().getName()))
            .definition(new DefinitionDto(apiDeploymentEntity.getApi()))
            .build();
    }
}
