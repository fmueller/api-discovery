package org.zalando.apidiscovery.crawler.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LegacyApiDefinition {

    public static final String STATUS_UNSUCCESSFUL = "UNSUCCESSFUL";
    public static final String STATUS_SUCCESS = "SUCCESS";

    public static LegacyApiDefinition UNSUCCESSFUL = LegacyApiDefinition.builder().status(STATUS_UNSUCCESSFUL).build();

    private String status;
    @Builder.Default
    private String type = ApiDefinition.UNDEFINED_SCHEMA_TYPE;
    @Builder.Default
    private String name = ApiDefinition.UNDEFINED_TITLE;
    @Builder.Default
    private String version = ApiDefinition.UNDEFINED_VERSION;
    private String serviceUrl;
    private String url;
    private String ui;
    private String definition;
}
