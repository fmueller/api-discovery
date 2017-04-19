package org.zalando.apidiscovery.storage.api;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
                                                              DeploymentLinkDto.DefinitionLinkDto definitionLinkDto) {
        return buildDefinitionDeploymentLink(
            builder,
            definitionLinkDto.getApiName(),
            definitionLinkDto.getApiVersion(),
            definitionLinkDto.getDefinitionId());
    }

    public static UriComponents buildDefinitionDeploymentLink(UriComponentsBuilder builder,
                                                              DeploymentDto.DeploymentDefinitionDto definitionDto) {
        return buildDefinitionDeploymentLink(
            builder,
            definitionDto.getApiName(),
            definitionDto.getApiVersion(),
            definitionDto.getApiDefinitionId());
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

    public static UriComponents buildDefinitionDeploymentLink(UriComponentsBuilder builder,
                                                              DeploymentDto.DeploymentApplicationDto deploymentApplicationDto) {
        return buildApplicationDeploymentLink(builder, deploymentApplicationDto.getName());
    }

    public static UriComponents buildLink(UriComponentsBuilder builder,
                                          DeploymentLinkDto deploymentLinkDto) {
        if (deploymentLinkDto instanceof DeploymentLinkDto.ApplicationLinkDto) {

            return buildApplicationDeploymentLink(builder, ((DeploymentLinkDto.ApplicationLinkDto) deploymentLinkDto).getApplicationName());
        } else if (deploymentLinkDto instanceof DeploymentLinkDto.DefinitionLinkDto) {

            return buildDefinitionDeploymentLink(builder, (DeploymentLinkDto.DefinitionLinkDto) deploymentLinkDto);
        } else {

            throw new UnsupportedOperationException(format("LinkBuilder for class [{}] not supported", deploymentLinkDto));
        }
    }


}
