package org.zalando.apidiscovery.crawler.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiDefinition {

    public static final String STATUS_UNSUCCESSFUL = "UNSUCCESSFUL";
    public static final String STATUS_SUCCESSFUL = "SUCCESSFUL";

    public static final String UNDEFINED_SCHEMA_TYPE = "undefined_schema_type";
    public static final String UNDEFINED_VERSION = "undefined_version";

    private String status;

    @Builder.Default
    private String type = UNDEFINED_SCHEMA_TYPE;
    private String apiName;
    private String appName;
    @Builder.Default
    private String version = UNDEFINED_VERSION;
    private String serviceUrl;
    private String url;
    private String ui;
    private String definition;
}
