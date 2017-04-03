package org.zalando.apidiscovery.storage.api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.valueOf;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
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

    private ApiLifecycleState aggregateApplicationLifecycleStateForApi(List<ApiEntity> apiEntities) {
        List<ApiDeploymentEntity> apiDeploymentList = apiEntities.stream()
            .flatMap(apiEntity -> apiEntity.getApiDeploymentEntities().stream())
            .collect(toList());
        return aggregateApplicationLifecycleStateForDeploymentEntities(apiDeploymentList);
    }

    private ApiLifecycleState aggregateApplicationLifecycleStateForDeploymentEntities(List<ApiDeploymentEntity> apiDeploymentEntities) {
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

        if (apiEntities.stream().findFirst().isPresent()) {
            ApiEntity apiEntity = apiEntities.stream().findFirst().get();

            return Optional.of(new ApiDto(apiEntity.getApiName(),
                aggregateApplicationLifecycleStateForApi(apiEntities),
                mapVersions(apiEntities),
                mapApplications(apiEntities)));

        }
        return Optional.empty();

    }

    private List<VersionsDto> mapVersions(List<ApiEntity> apiEntities) {
        return apiEntities.stream()
            .collect(groupingBy(ApiEntity::getApiVersion))
            .entrySet().stream()
            .map(entry -> new VersionsDto(entry.getKey(), aggregateApplicationLifecycleStateForApi(entry.getValue()),
                entry
                    .getValue().stream()
                    .map(this::mapApiEntityToApiDefinition)
                    .collect(toList()))
            )
            .collect(toList());
    }

    private ApiDefinitionDto mapApiEntityToApiDefinition(ApiEntity apiEntity) {
        List<DeploymentLinkDto> deploymentLinkDtos = apiEntity.getApiDeploymentEntities().stream()
            .map(this::mapApiDeploymentEntityToApplicationDeploymentLink)
            .collect(toList());

        return ApiDefinitionDto.builder()
            .id(valueOf(apiEntity.getId()))
            .definition(apiEntity.getDefinition())
            .type(apiEntity.getDefinitionType())
            .applications(deploymentLinkDtos)
            .build();
    }


    private DeploymentLinkDto mapApiDeploymentEntityToApplicationDeploymentLink(ApiDeploymentEntity apiDeploymentEntity) {
        return mapApiDeploymentEntityToDeploymentLink(apiDeploymentEntity)
            .linkBuilder(new ApplicationDeploymentLinkBuilder(apiDeploymentEntity.getApplication().getName()))
            .build();
    }

    private DeploymentLinkDto mapApiDeploymentEntityToDefinitionDeploymentLink(ApiDeploymentEntity apiDeploymentEntity) {
        ApiEntity apiEntity = apiDeploymentEntity.getApi();
        return mapApiDeploymentEntityToDeploymentLink(apiDeploymentEntity)
            .linkBuilder(new DefinitionDeploymentLinkBuilder(apiEntity.getApiName(), apiEntity.getApiVersion(), String.valueOf(apiEntity.getId())))
            .build();
    }


    private DeploymentLinkDto.DeploymentLinkDtoBuilder mapApiDeploymentEntityToDeploymentLink(ApiDeploymentEntity apiDeploymentEntity) {
        return DeploymentLinkDto.builder()
            .lifecycleState(apiDeploymentEntity.getLifecycleState())
            .apiUi(apiDeploymentEntity.getApiUi())
            .apiUrl(apiDeploymentEntity.getApiUrl())
            .created(apiDeploymentEntity.getCreated())
            .lastUpdated(apiDeploymentEntity.getLastCrawled());
    }

    private List<ApplicationDto> mapApplications(List<ApiEntity> apieEntities) {
        return applicationRepository.findByApiIds(apieEntities).stream()
            .map(this::mapApplicationToApplicationDto)
            .collect(toList());
    }


    private ApplicationDto mapApplicationToApplicationDto(ApplicationEntity applicationEntity) {
        List<DeploymentLinkDto> deploymentLinkDtos = applicationEntity.getApiDeploymentEntities().stream()
            .map(this::mapApiDeploymentEntityToDefinitionDeploymentLink)
            .collect(toList());

        return ApplicationDto.builder()
            .name(applicationEntity.getName())
            .appUrl(applicationEntity.getAppUrl())
            .definitions(deploymentLinkDtos)
            .build();
    }


}
