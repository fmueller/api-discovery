package org.zalando.apidiscovery.storage.api;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ApplicationEntityToApplicationDtoConverter {

    public static ApplicationDto toApplicationDto(ApplicationEntity applicationEntity) {
        List<DeploymentLinkDto> deploymentLinkDtos = null;
        if (applicationEntity.getApiDeploymentEntities() != null) {
            deploymentLinkDtos = applicationEntity.getApiDeploymentEntities().stream()
                    .map(ApplicationEntityToApplicationDtoConverter::mapApiDeploymentEntityToDefinitionDeploymentLink)
                    .collect(toList());
        }

        return ApplicationDto.builder()
                .name(applicationEntity.getName())
                .appUrl(applicationEntity.getAppUrl())
                .definitions(deploymentLinkDtos)
                .created(applicationEntity.getCreated())
                .build();
    }


    private static DeploymentLinkDto mapApiDeploymentEntityToDefinitionDeploymentLink(ApiDeploymentEntity apiDeploymentEntity) {
        ApiEntity apiEntity = apiDeploymentEntity.getApi();
        LinkBuilder linkBuilder = new DefinitionDeploymentLinkBuilder(apiEntity);
        return new DeploymentLinkDto(apiDeploymentEntity, linkBuilder);
    }

}
