package org.zalando.apidiscovery.storage.api;

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
