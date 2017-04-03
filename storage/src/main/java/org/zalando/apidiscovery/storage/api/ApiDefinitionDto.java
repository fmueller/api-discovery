package org.zalando.apidiscovery.storage.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiDefinitionDto {

    private String id;
    private String type;
    private String definition;
    private List<DeploymentLinkDto> applications;
}
