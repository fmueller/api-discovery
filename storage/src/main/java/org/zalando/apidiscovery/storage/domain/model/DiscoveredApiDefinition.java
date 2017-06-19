package org.zalando.apidiscovery.storage.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscoveredApiDefinition {

    @NotNull
    private DiscoveredApiDefinitionState status;

    @NotBlank
    private String apiName;

    @NotBlank
    private String appName;

    private String version;
    private String type;
    private String serviceUrl;
    private String url;
    private String ui;
    private String definition;
}
