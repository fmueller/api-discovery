package org.zalando.apidiscovery.storage.api;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import static java.lang.String.valueOf;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeploymentLinkDto {

    private String apiUrl;
    private String apiUi;
    private ApiLifecycleState lifecycleState;
    private OffsetDateTime created;
    private OffsetDateTime lastUpdated;
    private String href;

    @JsonIgnore
    private LinkBuilder linkBuilder;

    public DeploymentLinkDto(ApiDeploymentEntity apiDeploymentEntity, LinkBuilder linkBuilder) {
        lifecycleState = apiDeploymentEntity.getLifecycleState();
        apiUi = apiDeploymentEntity.getApiUi();
        apiUrl = apiDeploymentEntity.getApiUrl();
        created = apiDeploymentEntity.getCreated();
        lastUpdated = apiDeploymentEntity.getLastCrawled();
        this.linkBuilder = linkBuilder;
    }


}

interface LinkBuilder {
    String buildLink();
}

@Data
@AllArgsConstructor
class ApplicationDeploymentLinkBuilder implements LinkBuilder {

    private String applicationName;

    private UriComponentsBuilder uriComponentsBuilder;

    public ApplicationDeploymentLinkBuilder(ApplicationEntity application) {
        applicationName = application.getName();
    }

    @Override
    public String buildLink() {
        return getUriComponentsBuilder()
            .pathSegment("applications", applicationName)
            .toUriString();
    }

    private UriComponentsBuilder getUriComponentsBuilder() {
        if (uriComponentsBuilder != null) {
            return uriComponentsBuilder;
        }
        return UriComponentsBuilder.newInstance();
    }
}

@Data
@AllArgsConstructor
class DefinitionDeploymentLinkBuilder implements LinkBuilder {

    private String apiName;
    private String apiVersion;
    private String definitionId;
    private UriComponentsBuilder uriComponentsBuilder;

    public DefinitionDeploymentLinkBuilder(ApiEntity apiEntity) {
        apiName = apiEntity.getApiName();
        apiVersion = apiEntity.getApiVersion();
        definitionId = valueOf(apiEntity.getId());
    }

    @Override
    public String buildLink() {
        return getUriComponentsBuilder()
            .pathSegment("apis", apiName)
            .pathSegment("versions", apiVersion)
            .pathSegment("definitions", definitionId)
            .toUriString();
    }

    private UriComponentsBuilder getUriComponentsBuilder() {
        if (uriComponentsBuilder != null) {
            return uriComponentsBuilder;
        }
        return UriComponentsBuilder.newInstance();
    }
}

