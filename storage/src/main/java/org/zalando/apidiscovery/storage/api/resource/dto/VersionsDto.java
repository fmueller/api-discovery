package org.zalando.apidiscovery.storage.api.resource.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zalando.apidiscovery.storage.api.domain.ApiLifecycleState;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VersionsDto {

    private String apiVersion;
    private ApiLifecycleState lifecycleState;
    private List<ApiDefinitionDto> definitions;
}
