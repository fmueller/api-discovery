package org.zalando.apidiscovery.storage.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.*;

@Service
public class ApiService {

    @Autowired
    private ApiRepository apiRepository;

    public List<Api> getAllApis() {
        List<ApiEntity> apiEntities = apiRepository.findAll();

        return apiEntities
                .stream()
                .collect(groupingBy(ApiEntity::getApiName))
                .entrySet()
                .stream()
                .map(entry -> new Api(entry.getKey(), aggregateApplicationLifecycleState(entry.getValue())))
                .collect(toList());
    }

    private ApiLifecycleState aggregateApplicationLifecycleState(List<ApiEntity> apis) {
        if (apis.stream()
                .filter(apiEntity -> ACTIVE.equals(apiEntity.getLifecycleState())).count() > 0) {
            return ACTIVE;
        } else if (apis.stream()
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
