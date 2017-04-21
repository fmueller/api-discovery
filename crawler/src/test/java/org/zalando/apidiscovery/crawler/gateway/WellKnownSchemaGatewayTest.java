package org.zalando.apidiscovery.crawler.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.zalando.apidiscovery.crawler.TestDataHelper.metaApiApplication;
import static org.zalando.apidiscovery.crawler.TestDataHelper.readJson;

@RunWith(SpringJUnit4ClassRunner.class)
public class WellKnownSchemaGatewayTest {

    @Value("classpath:meta_api_schema_discovery.json")
    private Resource metaApiSchemaDiscoveryResource;

    @Value("classpath:meta_api_definition.json")
    private Resource metaApiDefinitionResource;

    @Mock
    RestTemplate restTemplate;

    private WellKnownSchemaGateway schemaGateway;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        schemaGateway = new WellKnownSchemaGateway(restTemplate);
    }

    @Test
    public void shouldSuccessfullyRetrieveSchemaDiscovery() throws Exception {
        JsonNode mockedSchemaDiscovery = readJson(metaApiSchemaDiscoveryResource);
        ResponseEntity<JsonNode> schemaResponse = new ResponseEntity<>(mockedSchemaDiscovery, HttpStatus.OK);
        doReturn(schemaResponse).when(restTemplate).exchange(
            eq("https://meta.api/.well-known/schema-discovery"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class));

        JsonNode schemaDiscovery = schemaGateway.retrieveSchemaDiscovery(metaApiApplication());

        assertThat(schemaDiscovery).isNotNull();
        assertThat(schemaDiscovery).isEqualTo(mockedSchemaDiscovery);
    }

    @Test
    public void shouldReturnNullIfNot2xxResponse() throws Exception {
        JsonNode mockedSchemaDiscovery = readJson(metaApiSchemaDiscoveryResource);
        ResponseEntity<JsonNode> schemaResponse = new ResponseEntity<>(mockedSchemaDiscovery, HttpStatus.NOT_FOUND);
        doReturn(schemaResponse).when(restTemplate).exchange(
            eq("https://meta.api/.well-known/schema-discovery"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class));

        JsonNode schemaDiscovery = schemaGateway.retrieveSchemaDiscovery(metaApiApplication());

        assertThat(schemaDiscovery).isNull();
    }

    @Test
    public void shouldSuccessfullyRetrieveApiDefinition() throws Exception {
        JsonNode mockedApiDefinition = readJson(metaApiDefinitionResource);
        JsonNode mockedSchemaDiscovery = readJson(metaApiSchemaDiscoveryResource);

        ResponseEntity<JsonNode> definitionResponse = new ResponseEntity<>(mockedApiDefinition, HttpStatus.OK);
        doReturn(definitionResponse).when(restTemplate).exchange(
            eq("https://meta.api/swagger.json"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class));

        JsonNode apiDefinition = schemaGateway.retrieveApiDefinition(metaApiApplication(), mockedSchemaDiscovery);

        assertThat(apiDefinition).isNotNull();
        assertThat(apiDefinition).isEqualTo(mockedApiDefinition);
    }

    @Test
    public void shouldTryToRetrieveYamlApiDefinitionIfSomethingGoesWrong() throws Exception {
        JsonNode mockedSchemaDiscovery = readJson(metaApiSchemaDiscoveryResource);
        doThrow(new RestClientException("oh no!")).when(restTemplate).exchange(
            eq("https://meta.api/swagger.json"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class));

        doReturn(new ResponseEntity<>("some yaml", HttpStatus.OK)).when(restTemplate).exchange(
            eq("https://meta.api/swagger.json"),
            eq(HttpMethod.GET),
            anyObject(),
            eq(String.class));

        schemaGateway.retrieveApiDefinition(metaApiApplication(), mockedSchemaDiscovery);

        InOrder inOrder = Mockito.inOrder(restTemplate);
        inOrder.verify(restTemplate).exchange(eq("https://meta.api/swagger.json"), eq(HttpMethod.GET), anyObject(),
            eq(JsonNode.class));
        inOrder.verify(restTemplate).exchange(eq("https://meta.api/swagger.json"), eq(HttpMethod.GET), anyObject(),
            eq(String.class));
    }

    @Test
    public void shouldExtractApiDefinitionUrl() throws Exception {
        String url = WellKnownSchemaGateway.extractApiDefinitionUrl(readJson(metaApiSchemaDiscoveryResource));

        assertThat(url).isEqualTo("swagger.json");
    }

    @Test
    public void shouldExtractServiceUrl() throws Exception {
        String serviceUrl = WellKnownSchemaGateway.extractServiceUrl(metaApiApplication());

        assertThat(serviceUrl).isEqualTo("https://meta.api/");
    }
}
