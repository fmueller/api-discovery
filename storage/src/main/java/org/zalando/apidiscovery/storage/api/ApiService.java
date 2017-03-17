package org.zalando.apidiscovery.storage.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.ACTIVE;
import static org.zalando.apidiscovery.storage.api.ApiLifecycleState.INACTIVE;

@Service
public class ApiService {

    @Autowired
    private ApiRepository apiRepository;

    public List<Api> getAllApis() {
        List<ApiEntity> apiEntities = apiRepository.findAll();

        Map<String, List<ApiEntity>> apisGroupedByNameMap = apiEntities
                .stream()
                .collect(groupingBy(ApiEntity::getApiName));

        List<Api> apiList = new ArrayList<>();
        for (Entry<String, List<ApiEntity>> entry : apisGroupedByNameMap.entrySet()) {
            boolean isApiActive = entry.getValue()
                    .stream()
                    .filter(apiEntity -> ACTIVE.equals(apiEntity.getLifecycleState())).count() > 0;

            apiList.add(new Api(entry.getKey(), isApiActive ? ACTIVE : INACTIVE));

        }
        return apiList;
    }

}
