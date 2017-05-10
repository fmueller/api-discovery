package org.zalando.apidiscovery.storage.domain.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.zalando.apidiscovery.storage.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.repository.ApiEntity;

import static java.lang.String.valueOf;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DeploymentLink {

    private String apiUrl;
    private String apiUi;
    private ApiLifecycleState lifecycleState;
    private OffsetDateTime created;
    private OffsetDateTime lastUpdated;
    private String href;


    public DeploymentLink(ApiDeploymentEntity apiDeploymentEntity) {
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
    public static class ApplicationLink extends DeploymentLink {

        @JsonIgnore
        private String applicationName;

        public ApplicationLink(ApiDeploymentEntity apiDeploymentEntity) {
            super(apiDeploymentEntity);
            this.applicationName = apiDeploymentEntity.getApplication().getName();
        }
    }


    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class DefinitionLink extends DeploymentLink {

        @JsonIgnore
        private String apiName;
        @JsonIgnore
        private String apiVersion;
        @JsonIgnore
        private String definitionId;

        public DefinitionLink(ApiDeploymentEntity apiDeploymentEntity) {
            super(apiDeploymentEntity);
            ApiEntity apiEntity = apiDeploymentEntity.getApi();
            apiName = apiEntity.getApiName();
            apiVersion = apiEntity.getApiVersion();
            definitionId = valueOf(apiEntity.getDefinitionId());
        }
    }

}