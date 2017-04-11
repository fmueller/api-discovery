package org.zalando.apidiscovery.storage.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiDto {

    private ApiMetaData apiMetaData;
    private List<VersionsDto> versions;
    private List<ApplicationDto> applications;


    public ApiDto(final String name, final ApiLifecycleState lifecycleState) {
        apiMetaData = new ApiMetaData(name, lifecycleState);
    }

    public ApiDto(final String name, final ApiLifecycleState lifecycleState, List<VersionsDto> versions, List<ApplicationDto> applications) {
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
