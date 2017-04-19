package org.zalando.apidiscovery.storage.api;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    class ApplicationLinkDto extends DeploymentLinkDto {

        @JsonIgnore
        private String applicationName;

        public ApplicationLinkDto(ApiDeploymentEntity apiDeploymentEntity, String applicationName) {
            super(apiDeploymentEntity);
            this.applicationName = applicationName;
        }
    }


    @Data
    @AllArgsConstructor
    class DefinitionLinkDto extends DeploymentLinkDto {

        @JsonIgnore
        private String apiName;
        @JsonIgnore
        private String apiVersion;
        @JsonIgnore
        private String definitionId;

        public DefinitionLinkDto(ApiDeploymentEntity apiDeploymentEntity, ApiEntity apiEntity) {
            super(apiDeploymentEntity);
            apiName = apiEntity.getApiName();
            apiVersion = apiEntity.getApiVersion();
            definitionId = valueOf(apiEntity.getId());
        }
    }

}