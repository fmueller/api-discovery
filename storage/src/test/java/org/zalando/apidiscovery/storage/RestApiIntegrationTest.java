package org.zalando.apidiscovery.storage;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.zalando.apidiscovery.storage.ApiLifecycleManager.ACTIVE;
import static org.zalando.apidiscovery.storage.ApiLifecycleManager.DECOMMISSIONED;
import static org.zalando.apidiscovery.storage.ApiLifecycleManager.INACTIVE;
import static org.zalando.apidiscovery.storage.TestDataHelper.createBasicApiDefinition;
import static org.zalando.apidiscovery.storage.TestDataHelper.createDecommissionedApiDefinition;
import static org.zalando.apidiscovery.storage.TestDataHelper.createInactiveApiDefinition;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class RestApiIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private ApiDefinitionRepository repository;

    @Autowired
    private MetricsCollector metricsCollector;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TestRestTemplate restTemplate;

    private ApiDefinition apiDefinition;

    private final String serviceId = "some-service";

    @Before
    public void initExampleData() {
        repository.deleteAll();

        apiDefinition = new ApiDefinition();
        apiDefinition.setApplicationId(serviceId);
        apiDefinition.setStatus("SUCCESS");
        apiDefinition.setDefinition("{\"swagger\": \"some api definition in here\"}");
    }

    @Test
    public void saveBaseApiDefinition() {
        saveApiDefinition();

        ResponseEntity<ApiDefinition> responseEntity = restTemplate.getForEntity("/apps/" + serviceId, ApiDefinition.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(apiDefinition);
    }

    @Test
    public void saveBaseApiDefinitionWithUnsuccessfulStatus() {
        apiDefinition.setStatus("NOT_SUCCESS");
        saveApiDefinition();

        ResponseEntity<ApiDefinition> responseEntity = restTemplate.getForEntity("/apps/" + serviceId, ApiDefinition.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(apiDefinition);
    }

    @Test
    public void retrieveAllApiDefinitions() {
        saveApiDefinition(serviceId);
        saveApiDefinition(serviceId + "-2");
        saveApiDefinition(serviceId + "-3");

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/apps", String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody())
                .contains(serviceId)
                .contains(serviceId + "-2")
                .contains(serviceId + "-3")
                .contains("status")
                .contains("name")
                .contains("version")
                .doesNotContain("some-other-service-id")
                .doesNotContain("definition");
    }

    @Test
    public void retrieveDefinitionOfOneApiDefinition() throws IOException {
        saveApiDefinition();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/apps/" + serviceId + "/definition", String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(mapper.readTree(apiDefinition.getDefinition())).isEqualTo(mapper.readTree(responseEntity.getBody()));
    }

    @Test
    public void stripOffTrainlingSlashesInServiceUrl() throws IOException {
        apiDefinition.setServiceUrl("my.service/");
        saveApiDefinition();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/apps/" + serviceId, String.class);

        assertThat(mapper.readTree(responseEntity.getBody()).get("service_url").textValue()).isEqualTo("my.service");
    }

    @Test
    public void addProtocolToServiceUrl() throws IOException {
        final ObjectNode definition = mapper.createObjectNode();
        definition.putArray("schemes").add("https");

        apiDefinition.setServiceUrl("my.service");
        apiDefinition.setDefinition(definition.toString());
        saveApiDefinition();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/apps/" + serviceId, String.class);
        assertThat(mapper.readTree(responseEntity.getBody()).get("service_url").textValue()).isEqualTo("https://my.service");
    }

    @Test
    public void addProtocolToServiceUrlOnlyIfItsNotAlreadyThere() throws IOException {
        final ObjectNode definition = mapper.createObjectNode();
        definition.putArray("schemes").add("https");

        apiDefinition.setServiceUrl("https://my.service");
        apiDefinition.setDefinition(definition.toString());
        saveApiDefinition();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/apps/" + serviceId, String.class);
        assertThat(mapper.readTree(responseEntity.getBody()).get("service_url").textValue()).isEqualTo("https://my.service");
    }

    @Test
    public void chooseFirstProtocolInSchemesArray() throws IOException {
        final ObjectNode definition = mapper.createObjectNode();
        definition.putArray("schemes").add("http").add("https").add("ws");

        apiDefinition.setServiceUrl("my.service");
        apiDefinition.setDefinition(definition.toString());
        saveApiDefinition();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/apps/" + serviceId, String.class);
        assertThat(mapper.readTree(responseEntity.getBody()).get("service_url").textValue()).isEqualTo("http://my.service");
    }

    @Test
    public void shouldListOnlyActiveApis() throws IOException {
        repository.save(createBasicApiDefinition());
        repository.save(createInactiveApiDefinition());
        repository.save(createDecommissionedApiDefinition());

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/apps?lifecycle_state=active", String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody())
                .contains(ACTIVE)
                .doesNotContain(INACTIVE)
                .doesNotContain(DECOMMISSIONED);
    }

    @Test
    public void shouldListOnlyInactiveApis() throws IOException {
        repository.save(createBasicApiDefinition());
        repository.save(createInactiveApiDefinition());
        repository.save(createDecommissionedApiDefinition());

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/apps?lifecycle_state=inactive", String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody())
                .contains(INACTIVE)
                .doesNotContain("\"" + ACTIVE + "\"") // necessary, otherwise INACTIVE would also match this
                .doesNotContain(DECOMMISSIONED);
    }

    @Test
    public void shouldListOnlyDecommissionedApis() throws IOException {
        repository.save(createBasicApiDefinition());
        repository.save(createInactiveApiDefinition());
        repository.save(createDecommissionedApiDefinition());

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/apps?lifecycle_state=decommissioned", String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(responseEntity.getBody())
                .contains(DECOMMISSIONED)
                .doesNotContain(ACTIVE)
                .doesNotContain(INACTIVE);
    }

    @Test
    public void addCreatedDateForNewApiDefinition() {
        saveApiDefinition();
        ApiDefinition apiDefinition = retrieveApiDefinition();
        assertThat(apiDefinition.getCreated()).isNotNull();
    }

    @Test
    public void setLastChangedDateAccordinglyForUpdatedApiDefinition() {
        saveApiDefinition();
        DateTime lastChanged = retrieveApiDefinition().getLastChanged();

        apiDefinition.setStatus("FAILED");
        apiDefinition.setLifecycleState(ACTIVE);
        saveApiDefinition();

        assertThat(lastChanged)
                .isNotNull()
                .isNotEqualTo(retrieveApiDefinition().getLastChanged());
    }

    @Test
    public void doesNotSetLastChangedDateIfNothingChanged() {
        saveApiDefinition();
        DateTime lastChanged = retrieveApiDefinition().getLastChanged();
        saveApiDefinition();

        assertThat(lastChanged)
                .isNotNull()
                .isEqualTo(retrieveApiDefinition().getLastChanged());
    }

    @Test
    public void setLastPersistedDateAccordinglyEvenIfNothingChanged() {
        saveApiDefinition();
        DateTime lastPersisted = retrieveApiDefinition().getLastPersisted();
        saveApiDefinition();

        assertThat(lastPersisted)
                .isNotNull()
                .isNotEqualTo(retrieveApiDefinition().getLastPersisted());
    }

    @Test
    public void retrieveMetricsAboutDifferentApiStates() throws InterruptedException {
        saveApiDefinition();
        metricsCollector.collectMetrics();

        ResponseEntity<JsonNode> metricsResponse = restTemplate.getForEntity("/metrics", JsonNode.class);
        assertThat(metricsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode rootObject = metricsResponse.getBody();
        assertThat(rootObject.has("gauge.apis.crawled.success")).isTrue();
    }

    private ApiDefinition retrieveApiDefinition() {
        return restTemplate.getForEntity("/apps/" + serviceId, ApiDefinition.class).getBody();
    }

    private void saveApiDefinition() {
        saveApiDefinition(serviceId);
    }

    private void saveApiDefinition(String serviceId) {
        apiDefinition.setApplicationId(serviceId);
        restTemplate.put("/apps/" + serviceId, apiDefinition);
    }
}
