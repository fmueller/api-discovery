package org.zalando.apidiscovery.storage.legacy;

/**
 * This DTO is used for list representation of {@link ApiDefinition}.
 */
class ApiDefinitionListDto {

    private final String applicationId;

    private final String status;

    private final String lifecycleState;

    private final String name;

    private final String version;

    public ApiDefinitionListDto(ApiDefinition apiDefinition) {
        this.applicationId = apiDefinition.getApplicationId();
        this.status = apiDefinition.getStatus();
        this.lifecycleState = apiDefinition.getLifecycleState();
        this.name = apiDefinition.getName();
        this.version = apiDefinition.getVersion();
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getStatus() {
        return status;
    }

    public String getLifecycleState() {
        return lifecycleState;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
