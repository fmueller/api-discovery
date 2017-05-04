package org.zalando.apidiscovery.storage.api.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiDefinitionDto {

    private String type;
    private String definition;
    private List<DeploymentLinkDto> applications;
}
