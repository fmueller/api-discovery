package org.zalando.apidiscovery.storage.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Application {

    private String name;
    private String appUrl;
    private List<DeploymentLink> definitions;
    private OffsetDateTime created;

}
