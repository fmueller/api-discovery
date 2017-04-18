package org.zalando.apidiscovery.storage;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.Resource;
import org.zalando.apidiscovery.storage.api.DiscoveredApiDefinition;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

public final class TestDataHelper {

    private TestDataHelper() {
    }

    public static ApiDefinition createUnsuccessfulApiDefinition() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setStatus("UNSUCCESSFUL");
        return apiDefinition;
    }

    public static ApiDefinition createInactiveApiDefinition() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setLifecycleState(ApiLifecycleManager.INACTIVE);
        return apiDefinition;
    }

    public static ApiDefinition createDecommissionedApiDefinition() {
        ApiDefinition apiDefinition = createBasicApiDefinition();
        apiDefinition.setLifecycleState(ApiLifecycleManager.DECOMMISSIONED);
        return apiDefinition;
    }

    public static ApiDefinition createBasicApiDefinition() {
        ApiDefinition apiDefinition = new ApiDefinition();
        apiDefinition.setApplicationId(RandomStringUtils.randomAlphabetic(20));
        apiDefinition.setStatus("SUCCESS");
        apiDefinition.setLifecycleState(ApiLifecycleManager.ACTIVE);
        apiDefinition.setLastPersisted(now(UTC));
        apiDefinition.setLastChanged(apiDefinition.getLastPersisted());
        return apiDefinition;
    }


    public static DiscoveredApiDefinition discoveredMetaApi(String version, String definitionDiff) {
        return DiscoveredApiDefinition.builder()
                .applicationName("Meta Application")
                .apiName("meta-api")
                .type("swagger")
                .version(version)
                .definition("{\"info\":{\"title\":\"Meta API\",\"version\":\"" + version + "\"}, \"diff\":\"" + definitionDiff + "\"}")
                .build();
    }

    public static String readResource(Resource resource) throws IOException {
        return Files.lines(resource.getFile().toPath()).collect(Collectors.joining());
    }
}
