package org.zalando.apidiscovery.storage;

import org.springframework.core.io.Resource;
import org.zalando.apidiscovery.storage.domain.model.DiscoveredApiDefinition;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class TestDataHelper {

    private TestDataHelper() {
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
