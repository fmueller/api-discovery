package org.zalando.apidiscovery.storage.api;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
