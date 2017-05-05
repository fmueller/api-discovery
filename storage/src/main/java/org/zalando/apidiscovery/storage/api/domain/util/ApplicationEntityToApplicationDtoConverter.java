package org.zalando.apidiscovery.storage.api.domain.util;

import org.zalando.apidiscovery.storage.api.domain.model.ApplicationDto;
import org.zalando.apidiscovery.storage.api.domain.model.DeploymentLinkDto;
import org.zalando.apidiscovery.storage.api.repository.ApplicationEntity;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ApplicationEntityToApplicationDtoConverter {

    public static ApplicationDto toApplicationDto(ApplicationEntity applicationEntity) {
        List<DeploymentLinkDto> deploymentLinkDtos = null;
        if (applicationEntity.getApiDeploymentEntities() != null) {
            deploymentLinkDtos = applicationEntity.getApiDeploymentEntities().stream()
                .map(apiDeploymentEntity -> new DeploymentLinkDto.DefinitionLinkDto(apiDeploymentEntity))
                .collect(toList());
        }

        return ApplicationDto.builder()
            .name(applicationEntity.getName())
            .appUrl(applicationEntity.getAppUrl())
            .definitions(deploymentLinkDtos)
            .created(applicationEntity.getCreated())
            .build();
    }
}
