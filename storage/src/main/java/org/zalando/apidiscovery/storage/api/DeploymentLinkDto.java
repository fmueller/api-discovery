package org.zalando.apidiscovery.storage.api;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static java.lang.String.valueOf;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DeploymentLinkDto {

    private String apiUrl;
    private String apiUi;
    private ApiLifecycleState lifecycleState;
    private OffsetDateTime created;
    private OffsetDateTime lastUpdated;
    private String href;


    public DeploymentLinkDto(ApiDeploymentEntity apiDeploymentEntity) {
        lifecycleState = apiDeploymentEntity.getLifecycleState();
        apiUi = apiDeploymentEntity.getApiUi();
        apiUrl = apiDeploymentEntity.getApiUrl();
        created = apiDeploymentEntity.getCreated();
        lastUpdated = apiDeploymentEntity.getLastCrawled();
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class ApplicationLinkDto extends DeploymentLinkDto {

        @JsonIgnore
        private String applicationName;

        public ApplicationLinkDto(ApiDeploymentEntity apiDeploymentEntity) {
            super(apiDeploymentEntity);
            this.applicationName = apiDeploymentEntity.getApplication().getName();
        }
    }


    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class DefinitionLinkDto extends DeploymentLinkDto {

        @JsonIgnore
        private String apiName;
        @JsonIgnore
        private String apiVersion;
        @JsonIgnore
        private String definitionId;

        public DefinitionLinkDto(ApiDeploymentEntity apiDeploymentEntity) {
            super(apiDeploymentEntity);
            ApiEntity apiEntity = apiDeploymentEntity.getApi();
            apiName = apiEntity.getApiName();
            apiVersion = apiEntity.getApiVersion();
            definitionId = valueOf(apiEntity.getDefinitionId());
        }
    }

}