package org.zalando.apidiscovery.storage.api;

import lombok.Data;

@Data
public class Api {

    private ApiMetaData apiMetaData;

    @Data
    private class ApiMetaData {
        private String name;
        private String lifecycleState;
    }
}
