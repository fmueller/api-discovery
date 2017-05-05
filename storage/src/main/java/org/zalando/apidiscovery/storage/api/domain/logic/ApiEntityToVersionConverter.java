package org.zalando.apidiscovery.storage.api.domain.logic;

import org.zalando.apidiscovery.storage.api.domain.model.Versions;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.zalando.apidiscovery.storage.api.domain.logic.ApiService.aggregateApplicationLifecycleStateForApi;

public class ApiEntityToVersionConverter {

    public static List<Versions> toVersionList(List<ApiEntity> apiEntities) {
        apiEntitiesMustHaveSameName(apiEntities);
        return apiEntities.stream()
                .collect(groupingBy(ApiEntity::getApiVersion))
                .entrySet().stream()
                .map(entry -> new Versions(entry.getKey(), aggregateApplicationLifecycleStateForApi(entry.getValue()),
                        entry
                                .getValue().stream()
                                .map(ApiEntityToApiDefinitionConverter::toApiDefinition)
                                .collect(toList()))
                )
                .collect(toList());
    }

    private static void apiEntitiesMustHaveSameName(List<ApiEntity> apiEntities) {
        if (apiEntities.stream()
                .collect(groupingBy(ApiEntity::getApiName)).size() > 1) {
            throw new IllegalArgumentException("ApiEntities must have same name");
        }
    }
}

