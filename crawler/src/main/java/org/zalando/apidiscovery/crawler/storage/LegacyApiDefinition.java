package org.zalando.apidiscovery.crawler.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LegacyApiDefinition {

    public static LegacyApiDefinition UNSUCCESSFUL = LegacyApiDefinition.builder().status("UNSUCCESSFUL").build();

    private String status;
    private String type;
    private String name;
    private String version;
    private String serviceUrl;
    private String url;
    private String ui;
    private String definition;
}
