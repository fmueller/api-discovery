package org.zalando.apidiscovery.crawler.storage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiDefinition {

    public static ApiDefinition UNSUCCESSFUL = new ApiDefinition(
            "UNSUCCESSFUL", null, null, null, null, null, null, null, null
    );

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

    public ApiDefinition() {
    }

    public ApiDefinition(String status, String type,
                         String apiName, String appName,
                         String version, String serviceUrl,
                         String schemaUrl, String uiLink,
                         String definition) {
        this.status = status;
        this.type = type;
        this.apiName = apiName;
        this.appName = appName;
        this.version = version;
        this.serviceUrl = serviceUrl;
        this.schemaUrl = schemaUrl;
        this.uiLink = uiLink;
        this.definition = definition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getSchemaUrl() {
        return schemaUrl;
    }

    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    public String getUiLink() {
        return uiLink;
    }

    public void setUiLink(String uiLink) {
        this.uiLink = uiLink;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}