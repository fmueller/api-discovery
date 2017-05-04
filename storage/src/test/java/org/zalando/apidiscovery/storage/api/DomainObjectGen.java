package org.zalando.apidiscovery.storage.api;

import org.zalando.apidiscovery.storage.api.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;
import org.zalando.apidiscovery.storage.api.domain.ApiLifecycleState;
import org.zalando.apidiscovery.storage.api.repository.ApplicationEntity;

import java.time.OffsetDateTime;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

public class DomainObjectGen {

    public final static String APP_URL = "url";
    public final static String APP1_NAME = "app1";
    public final static String API_NAME = "test-api";
    public final static String API_VERSION_1 = "v1";
    public final static String API_VERSION_2 = "v2";
    public final static String API_URL = "http://localhost:8080/api";
    public final static String API_UI = "http://localhost:8080/ui";
    public final static String DEFINITION_TYPE = "swagger";
    public final static String DEFINITION = "API";
    public final static String DEFINITION_HASH = "f1";
    public final static int DEFINITION_ID = 1;
    public final static OffsetDateTime NOW = now(UTC);
    public final static ApiLifecycleState LIFECYCLE_STATE = ApiLifecycleState.ACTIVE;

    public static ApiDeploymentEntity givenApiDeployment(ApiEntity apiEntity, ApplicationEntity applicationEntity, ApiLifecycleState state) {
        return ApiDeploymentEntity.builder()
            .api(apiEntity)
            .application(applicationEntity)
            .apiUi(API_UI)
            .apiUrl(API_URL)
            .lifecycleState(state)
            .created(NOW)
            .lastCrawled(NOW)
            .build();
    }

    public static ApiDeploymentEntity givenApiDeployment(ApiEntity apiEntity, ApplicationEntity applicationEntity) {
        return givenApiDeployment(apiEntity, applicationEntity, LIFECYCLE_STATE);
    }

    public static ApiEntity givenApiEntity(long id, String name, String version) {
        return ApiEntity.builder()
            .id(id)
            .apiName(name)
            .apiVersion(version)
            .definitionType(DEFINITION_TYPE)
            .created(NOW)
            .definition(DEFINITION)
            .definitionId(DEFINITION_ID)
            .definitionHash(DEFINITION_HASH)
            .build();
    }

    public static ApiEntity givenApiEntity() {
        return givenApiEntity(API_NAME, API_VERSION_1, DEFINITION_ID);
    }

    public static ApiEntity givenApiEntity(String name, String version, int definitionId) {
        return ApiEntity.builder()
            .apiName(name)
            .apiVersion(version)
            .definitionType(DEFINITION_TYPE)
            .definitionHash(DEFINITION_HASH)
            .definitionId(definitionId)
            .created(NOW)
            .definition(DEFINITION)
            .build();
    }

    public static ApplicationEntity givenApplication(String name) {
        return ApplicationEntity.builder()
            .appUrl(APP_URL)
            .name(name)
            .created(NOW)
            .build();
    }

    public static ApplicationEntity givenApplication() {
        return givenApplication(APP1_NAME);
    }
}
