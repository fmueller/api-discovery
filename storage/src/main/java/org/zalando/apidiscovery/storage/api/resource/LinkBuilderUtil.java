package org.zalando.apidiscovery.storage.api.resource;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.apidiscovery.storage.api.domain.model.DeploymentLink;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;

import static java.lang.String.valueOf;
import static java.text.MessageFormat.format;

public class LinkBuilderUtil {


    public static UriComponents buildDefinitionDeploymentLink(UriComponentsBuilder builder,
                                                              ApiEntity apiEntity) {
        return buildDefinitionDeploymentLink(
            builder,
            apiEntity.getApiName(),
            apiEntity.getApiVersion(),
            valueOf(apiEntity.getDefinitionId()));
    }

    public static UriComponents buildDefinitionDeploymentLink(UriComponentsBuilder builder,
                                                              String apiName,
                                                              String apiVersion,
                                                              String definitionId) {
        return builder
            .pathSegment("apis", apiName)
            .pathSegment("versions", apiVersion)
            .pathSegment("definitions", definitionId)
            .build();
    }

    public static UriComponents buildApplicationDeploymentLink(UriComponentsBuilder builder,
                                                               String applicationName) {
        return builder
            .pathSegment("applications", applicationName)
            .build();
    }

    public static UriComponents buildLink(UriComponentsBuilder builder,
                                          DeploymentLink deploymentLink) {
        if (deploymentLink instanceof DeploymentLink.ApplicationLink) {

            return buildApplicationDeploymentLink(builder, ((DeploymentLink.ApplicationLink) deploymentLink).getApplicationName());
        } else if (deploymentLink instanceof DeploymentLink.DefinitionLink) {

            return buildDefinitionDeploymentLink(builder, (DeploymentLink.DefinitionLink) deploymentLink);
        } else {

            throw new UnsupportedOperationException(format("LinkBuilder for class [{0}] not supported", deploymentLink));
        }
    }

    public static UriComponents buildDefinitionDeploymentLink(UriComponentsBuilder builder,
                                                              DeploymentLink.DefinitionLink definitionLinkDto) {
        return buildDefinitionDeploymentLink(
            builder,
            definitionLinkDto.getApiName(),
            definitionLinkDto.getApiVersion(),
            definitionLinkDto.getDefinitionId());
    }

}
