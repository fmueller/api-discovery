package org.zalando.apidiscovery.storage.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Versions {

    private String apiVersion;
    private ApiLifecycleState lifecycleState;
    private List<ApiDefinition> definitions;
}
