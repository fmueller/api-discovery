package org.zalando.apidiscovery.storage.legacy;

import org.apache.commons.lang3.RandomStringUtils;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

final class LegacyTestDataHelper {

    private LegacyTestDataHelper() {
    }

    protected static ApiDefinition createUnsuccessfulApiDefinition() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setStatus("UNSUCCESSFUL");
        return apiDefinition;
    }

    protected static ApiDefinition createInactiveApiDefinition() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setLifecycleState(ApiLifecycleManager.INACTIVE);
        return apiDefinition;
    }

    protected static ApiDefinition createDecommissionedApiDefinition() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setLifecycleState(ApiLifecycleManager.DECOMMISSIONED);
        return apiDefinition;
    }

    protected static ApiDefinition createBasicApiDefinition() {
        ApiDefinition apiDefinition = new ApiDefinition();
        apiDefinition.setApplicationId(RandomStringUtils.randomAlphabetic(20));
        apiDefinition.setStatus("SUCCESS");
        apiDefinition.setLifecycleState(ApiLifecycleManager.ACTIVE);
        apiDefinition.setLastPersisted(now(UTC));
        apiDefinition.setLastChanged(apiDefinition.getLastPersisted());
        return apiDefinition;
    }

}
