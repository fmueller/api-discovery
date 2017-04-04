package org.zalando.apidiscovery.storage.api;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrawledApiDefinitionDto {

    /* the current status of the crawling */
    private String status;
    /* type of API definition */
    private String type;
    /* name of the API */
    private String apiName;
    /* version of the API */
    private String version;
    /* name of the application which provides this API */
    private String applicationName;
    /* URL pointing to the Application which is providing the API */
    private String serviceUrl;
    /* path to the API definition file */
    private String url;
    /* path to the UI for browsing the API */
    private String ui;
    /* API definition */
    private String definition;
}
