package org.zalando.apidiscovery.storage.api.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Api {

    private ApiMetaData apiMetaData;
    private List<Versions> versions;
    private List<Application> applications;


    public Api(final String name, final ApiLifecycleState lifecycleState) {
        apiMetaData = new ApiMetaData(name, lifecycleState);
    }

    public Api(final String name, final ApiLifecycleState lifecycleState, List<Versions> versions, List<Application> applications) {
        apiMetaData = new ApiMetaData(name, lifecycleState);
        this.versions = versions;
        this.applications = applications;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ApiMetaData {
        private String name;
        private ApiLifecycleState lifecycleState;
    }
}
