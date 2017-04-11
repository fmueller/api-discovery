package org.zalando.apidiscovery.storage.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscoveredApiDefinition {

    private String status;
    private String type;
    private String apiName;
    private String version;
    private String applicationName;
    private String serviceUrl;
    private String url;
    private String ui;
    private String definition;
}
