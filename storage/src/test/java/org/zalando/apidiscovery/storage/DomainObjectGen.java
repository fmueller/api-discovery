package org.zalando.apidiscovery.storage;

import java.time.OffsetDateTime;

import org.zalando.apidiscovery.storage.api.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.ApiEntity;
import org.zalando.apidiscovery.storage.api.ApiLifecycleState;
import org.zalando.apidiscovery.storage.api.ApplicationEntity;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

public class DomainObjectGen {

    public final static String APP_URL = "url";
    public final static String APP_NAME = "app1";
    public final static String API_NAME = "api1";
    public final static String API_VERSION = "v1";
    public final static String API_URL = "http://localhost:8080/api";
    public final static String API_UI = "http://localhost:8080/ui";
    public final static String DEFINITION_TYPE = "swagger";
    public final static String DEFINITION = "API";
    public final static long DEFINITION_ID = 1;
    public final static OffsetDateTime NOW = now(UTC);
    public final static ApiLifecycleState LIFECYCLE_STATE = ApiLifecycleState.ACTIVE;


    public static ApiDeploymentEntity givenApiDeployment(ApiEntity apiEntity, ApplicationEntity applicationEntity) {
        return ApiDeploymentEntity.builder()
            .api(apiEntity)
            .application(applicationEntity)
            .apiUi(API_UI)
            .apiUrl(API_URL)
            .lifecycleState(LIFECYCLE_STATE)
            .created(NOW)
            .lastCrawled(NOW)
            .build();
    }

    public static ApiEntity givenApiEntity(long id, String name, String version) {
        return ApiEntity.builder()
            .id(id)
            .apiName(name)
            .apiVersion(version)
            .definitionType(DEFINITION_TYPE)
            .created(NOW)
            .definition(DEFINITION)
            .build();
    }

    public static ApiEntity givenApiEntity() {
        return givenApiEntity(DEFINITION_ID, API_NAME, API_VERSION);
    }


    public static ApplicationEntity givenApplication() {
        return ApplicationEntity.builder()
            .appUrl(APP_URL)
            .name(APP_NAME)
            .created(NOW)
            .build();
    }
}
