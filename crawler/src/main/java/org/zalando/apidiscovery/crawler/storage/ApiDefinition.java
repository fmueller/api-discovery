package org.zalando.apidiscovery.crawler.storage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiDefinition {

    public static ApiDefinition UNSUCCESSFUL = ApiDefinition.builder().status("UNSUCCESSFUL").build();

    @JsonProperty("status")
    private String status;

    @JsonProperty("type")
    private String type;

    @JsonProperty("api_name")
    private String apiName;

    @JsonProperty("app_name")
    private String appName;

    @JsonProperty("version")
    private String version;

    @JsonProperty("service_url")
    private String serviceUrl;

    @JsonProperty("url")
    private String schemaUrl;

    @JsonProperty("ui")
    private String uiLink;

    @JsonProperty("definition")
    private String definition;

}
