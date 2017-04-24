package org.zalando.apidiscovery.crawler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class CrawlResult {

    private boolean successful;
    @Builder.Default
    private String reason = "";

}
