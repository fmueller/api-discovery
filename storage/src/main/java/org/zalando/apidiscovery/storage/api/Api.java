package org.zalando.apidiscovery.storage.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Api {

    private ApiMetaData apiMetaData;

    public Api(final String name, final ApiLifecycleState lifecycleState) {
        apiMetaData = new ApiMetaData(name, lifecycleState);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ApiMetaData {
        private String name;
        private ApiLifecycleState lifecycleState;
    }
}
