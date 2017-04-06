package org.zalando.apidiscovery.storage.api;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.zalando.apidiscovery.storage.api.ApiService.aggregateApplicationLifecycleStateForApi;

public class ApiEntityToVersionConverter {

    public static List<VersionsDto> toVersionDtoList(List<ApiEntity> apiEntities) {
        apiEntitiesMustHaveSameName(apiEntities);
        return apiEntities.stream()
            .collect(groupingBy(ApiEntity::getApiVersion))
            .entrySet().stream()
            .map(entry -> new VersionsDto(entry.getKey(), aggregateApplicationLifecycleStateForApi(entry.getValue()),
                entry
                    .getValue().stream()
                    .map(ApiEntityToApiDefinitionConverter::toApiDefinitionDto)
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

