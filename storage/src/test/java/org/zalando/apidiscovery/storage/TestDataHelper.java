package org.zalando.apidiscovery.storage;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

final public class TestDataHelper {

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

    public static String crawledUberApiDefinitionRequestBody() throws IOException, URISyntaxException {
        return readFile("uber.json");
    }

    public static String instagramSwaggerDefinitionJson() throws IOException, URISyntaxException {
        return readFile("instagram-api-definition.json");
    }

    private static String readFile(String fileName) throws URISyntaxException, IOException {
        URI fileLocation = TestDataHelper.class.getClassLoader().getResource(fileName).toURI();
        Path path = Paths.get(fileLocation);
        return Files.lines(path).collect(Collectors.joining());
    }
}
