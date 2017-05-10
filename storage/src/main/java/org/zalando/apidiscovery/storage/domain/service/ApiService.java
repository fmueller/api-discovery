package org.zalando.apidiscovery.storage.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState;
import org.zalando.apidiscovery.storage.domain.model.ApiDefinition;
import org.zalando.apidiscovery.storage.domain.model.Api;
import org.zalando.apidiscovery.storage.domain.model.Application;
import org.zalando.apidiscovery.storage.domain.model.Deployment;
import org.zalando.apidiscovery.storage.domain.model.Versions;
import org.zalando.apidiscovery.storage.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.repository.ApiEntity;
import org.zalando.apidiscovery.storage.repository.ApiRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.zalando.apidiscovery.storage.domain.service.ApiEntityToVersionConverter.toVersionList;
import static org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState.ACTIVE;
import static org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState.DECOMMISSIONED;
import static org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState.INACTIVE;

@Service
public class ApiService {

    private final ApplicationService applicationService;
    private final ApiRepository apiRepository;

    @Autowired
    public ApiService(ApiRepository apiRepository, ApplicationService applicationService) {
        this.apiRepository = apiRepository;
        this.applicationService = applicationService;
    }

    public List<Api> getAllApis() {
        List<ApiEntity> apiEntities = apiRepository.findAll();

        return apiEntities
            .stream()
            .collect(groupingBy(ApiEntity::getApiName))
            .entrySet().stream()
            .map(entry -> new Api(entry.getKey(), aggregateApplicationLifecycleStateForApi(entry.getValue())))
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

    public List<Api> getAllApis(ApiLifecycleState filterByLifecycleState) {
        return getAllApis().stream()
            .filter(api -> filterByLifecycleState.equals(api.getApiMetaData().getLifecycleState()))
            .collect(toList());
    }

    public Optional<Api> getApi(String apiName) {
        List<ApiEntity> apiEntities = apiRepository.findByApiName(apiName);

        if (!apiEntities.isEmpty()) {
            ApiLifecycleState lifecycleState = aggregateApplicationLifecycleStateForApi(apiEntities);
            List<Versions> versions = toVersionList(apiEntities);
            List<Application> applications = applicationService.getApplicationsByApiEntities(apiEntities);

            return Optional.of(new Api(apiName,
                lifecycleState,
                versions,
                applications));
        }
        return Optional.empty();
    }

    public List<Versions> getVersionsForApi(String apiId) {
        List<ApiEntity> apiEntities = apiRepository.findByApiName(apiId);
        return apiEntities.isEmpty() ? Collections.emptyList() : toVersionList(apiEntities);
    }

    public List<Versions> getVersionsForApi(String apiId, ApiLifecycleState lifecycleState) {
        List<ApiEntity> apiEntities = getApiEntityByLifecycleState(apiId, lifecycleState);
        return apiEntities.isEmpty() ? Collections.emptyList() : toVersionList(apiEntities);
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

    public Optional<Versions> getVersion(String apiId, String version) {
        List<ApiEntity> apiEntities = apiRepository.findByApiNameAndApiVersion(apiId, version);
        return toVersionList(apiEntities).stream()
            .findAny();
    }

    public Optional<ApiDefinition> getApiDefinitionDto(String apiId, String version, String definitionId) {
        try {
            return apiRepository
                .findByApiNameAndApiVersionAndDefinitionId(apiId, version, Integer.valueOf(definitionId))
                .map(ApiEntityToApiDefinitionConverter::toApiDefinition);
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }

    public List<Deployment> getDeploymentsForApi(String apiId) {
        return apiRepository.findByApiName(apiId).stream()
            .flatMap(apiEntity -> apiEntityToDeploymentDtoList(apiEntity).stream())
            .collect(toList());
    }

    private List<Deployment> apiEntityToDeploymentDtoList(ApiEntity apiEntity) {
        return apiEntity.getApiDeploymentEntities().stream()
            .map(apiDeploymentEntity -> apiDeploymentToDeploymentDto(apiDeploymentEntity))
            .collect(toList());
    }

    private Deployment apiDeploymentToDeploymentDto(ApiDeploymentEntity apiDeploymentEntity) {
        return Deployment.builder()
            .apiVersion(apiDeploymentEntity.getApi().getApiVersion())
            .application(new Deployment.ApplicationDto(apiDeploymentEntity.getApplication().getName()))
            .definition(new Deployment.DefinitionDto(apiDeploymentEntity.getApi()))
            .build();
    }
}
