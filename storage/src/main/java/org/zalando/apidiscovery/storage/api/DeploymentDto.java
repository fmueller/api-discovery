package org.zalando.apidiscovery.storage.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeploymentDto {

    private String apiVersion;

    private DeploymentApplicationDto application;

    private DeploymentDefinitionDto definition;


    public void buildLinks(UriComponentsBuilder builder) {
        definition.buildLink(builder.cloneBuilder());
        application.buildLink(builder.cloneBuilder());
    }

    @Data
    @NoArgsConstructor
    static class DeploymentApplicationDto {
        private String name;
        private String href;

        @JsonIgnore
        private ApplicationDeploymentLinkBuilder linkBuilder;

        public DeploymentApplicationDto(String name, ApplicationDeploymentLinkBuilder linkBuilder) {
            this.name = name;
            this.linkBuilder = linkBuilder;
        }

        public void buildLink(UriComponentsBuilder builder) {
            if (linkBuilder != null) {
                linkBuilder.setUriComponentsBuilder(builder);
                href = linkBuilder.buildLink();
            }
        }

    }

    @Data
    @NoArgsConstructor
    static class DeploymentDefinitionDto {
        private String href;

        @JsonIgnore
        private DefinitionDeploymentLinkBuilder linkBuilder;

        public DeploymentDefinitionDto(DefinitionDeploymentLinkBuilder linkBuilder) {
            this.linkBuilder = linkBuilder;
        }

        public void buildLink(UriComponentsBuilder builder) {
            if (linkBuilder != null) {
                linkBuilder.setUriComponentsBuilder(builder);
                href = linkBuilder.buildLink();
            }
        }

    }

}
