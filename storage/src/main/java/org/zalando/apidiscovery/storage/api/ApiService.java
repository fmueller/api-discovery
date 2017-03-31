package org.zalando.apidiscovery.storage.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.ACTIVE;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.DECOMMISSIONED;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.INACTIVE;

@Service
public class ApiService {

    private final ApiRepository apiRepository;

    @Autowired
    public ApiService(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    public List<Api> getAllApis() {
        List<ApiEntity> apiEntities = apiRepository.findAll();

        return apiEntities
            .stream()
            .collect(groupingBy(ApiEntity::getApiName))
            .entrySet()
            .stream()
            .map(entry -> new Api(entry.getKey(), aggregateApplicationLifecycleStateForApi(entry.getValue())))
            .collect(toList());
    }

    private ApiLifecycleState aggregateApplicationLifecycleStateForApi(List<ApiEntity> apiEntities) {
        List<ApiDeploymentEntity> apiDeploymentList = apiEntities
            .stream()
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

    public List<Api> getAllApis(ApiLifecycleState filterByLifecycleState) {
        return getAllApis().stream()
            .filter(api -> filterByLifecycleState.equals(api.getApiMetaData().getLifecycleState()))
            .collect(toList());
    }

}
