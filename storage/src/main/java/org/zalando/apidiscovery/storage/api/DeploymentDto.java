package org.zalando.apidiscovery.storage.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import static java.lang.String.valueOf;
import static org.zalando.apidiscovery.storage.api.LinkBuilderUtil.buildApplicationDeploymentLink;
import static org.zalando.apidiscovery.storage.api.LinkBuilderUtil.buildDefinitionDeploymentLink;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeploymentDto {

    private String apiVersion;

    private ApplicationDto application;

    private DefinitionDto definition;


    public void buildLinks(UriComponentsBuilder builder) {
        application.buildLink(builder.cloneBuilder());
        definition.buildLink(builder.cloneBuilder());
    }

    @Data
    @NoArgsConstructor
    static class ApplicationDto {
        private String name;
        private String href;

        public ApplicationDto(String name) {
            this.name = name;
        }

        public void buildLink(UriComponentsBuilder builder) {
            href = buildApplicationDeploymentLink(builder, name).toUriString();
        }

    }

    @Data
    @NoArgsConstructor
    static class DefinitionDto {
        private String href;

        @JsonIgnore
        private String apiName;

        @JsonIgnore
        private String apiVersion;

        @JsonIgnore
        private String apiDefinitionId;

        public DefinitionDto(ApiEntity apiEntity) {
            apiName = apiEntity.getApiName();
            apiVersion = apiEntity.getApiVersion();
            apiDefinitionId = valueOf(apiEntity.getDefinitionId());
        }

        public void buildLink(UriComponentsBuilder builder) {
            href = buildDefinitionDeploymentLink(builder, apiName, apiVersion, apiDefinitionId).toUriString();
        }

    }

}