package org.zalando.apidiscovery.storage.api.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VersionsDto {

    private String apiVersion;
    private ApiLifecycleState lifecycleState;
    private List<ApiDefinitionDto> definitions;
}
