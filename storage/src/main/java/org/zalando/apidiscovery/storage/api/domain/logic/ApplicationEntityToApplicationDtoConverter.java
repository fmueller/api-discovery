package org.zalando.apidiscovery.storage.api.domain.logic;

import org.zalando.apidiscovery.storage.api.domain.model.Application;
import org.zalando.apidiscovery.storage.api.domain.model.DeploymentLink;
import org.zalando.apidiscovery.storage.api.repository.ApplicationEntity;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ApplicationEntityToApplicationDtoConverter {

    public static Application toApplication(ApplicationEntity applicationEntity) {
        List<DeploymentLink> deploymentLinks = null;
        if (applicationEntity.getApiDeploymentEntities() != null) {
            deploymentLinks = applicationEntity.getApiDeploymentEntities().stream()
                .map(apiDeploymentEntity -> new DeploymentLink.DefinitionLink(apiDeploymentEntity))
                .collect(toList());
        }

        return Application.builder()
            .name(applicationEntity.getName())
            .appUrl(applicationEntity.getAppUrl())
            .definitions(deploymentLinks)
            .created(applicationEntity.getCreated())
            .build();
    }
}
