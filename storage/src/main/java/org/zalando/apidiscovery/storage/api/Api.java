package org.zalando.apidiscovery.storage.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Api {

    private ApiMetaData apiMetaData;

    public Api(final String name, final String lifecycleState) {
        apiMetaData = new ApiMetaData(name, lifecycleState);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class ApiMetaData {
        private String name;
        private String lifecycleState;
    }
}
