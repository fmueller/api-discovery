package org.zalando.apidiscovery.storage.api;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationDto {

    private String name;
    private String appUrl;
    private List<DeploymentLinkDto> definitions;
    private OffsetDateTime created;

}
