package org.zalando.apidiscovery.storage.api.domain.util;

import org.zalando.apidiscovery.storage.api.domain.dto.ApiDefinitionDto;
import org.zalando.apidiscovery.storage.api.domain.dto.DeploymentLinkDto;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ApiEntityToApiDefinitionConverter {

    public static ApiDefinitionDto toApiDefinitionDto(ApiEntity apiEntity) {
        List<DeploymentLinkDto> deploymentLinkDtos = null;
        if (apiEntity.getApiDeploymentEntities() != null) {
            deploymentLinkDtos = apiEntity.getApiDeploymentEntities().stream()
                .map(DeploymentLinkDto.ApplicationLinkDto::new)
                .collect(toList());
        }

        return ApiDefinitionDto.builder()
            .definition(apiEntity.getDefinition())
            .type(apiEntity.getDefinitionType())
            .applications(deploymentLinkDtos)
            .build();
    }
}
