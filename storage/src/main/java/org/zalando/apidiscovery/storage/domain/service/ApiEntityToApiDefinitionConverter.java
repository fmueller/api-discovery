package org.zalando.apidiscovery.storage.domain.service;

import org.zalando.apidiscovery.storage.domain.model.ApiDefinition;
import org.zalando.apidiscovery.storage.domain.model.DeploymentLink;
import org.zalando.apidiscovery.storage.repository.ApiEntity;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ApiEntityToApiDefinitionConverter {

    public static ApiDefinition toApiDefinition(ApiEntity apiEntity) {
        List<DeploymentLink> deploymentLinks = null;
        if (apiEntity.getApiDeploymentEntities() != null) {
            deploymentLinks = apiEntity.getApiDeploymentEntities().stream()
                .map(DeploymentLink.ApplicationLink::new)
                .collect(toList());
        }

        return ApiDefinition.builder()
            .definition(apiEntity.getDefinition())
            .type(apiEntity.getDefinitionType())
            .applications(deploymentLinks)
            .build();
    }
}
