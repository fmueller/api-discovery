package org.zalando.apidiscovery.crawler.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.zalando.apidiscovery.crawler.SchemaDiscovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.zalando.apidiscovery.crawler.TestDataHelper.metaApiKioApplication;
import static org.zalando.apidiscovery.crawler.TestDataHelper.parseResource;

@RunWith(SpringJUnit4ClassRunner.class)
public class WellKnownSchemaGatewayTest {

    @Value("classpath:meta_api_schema_discovery.json")
    private Resource metaApiSchemaDiscoveryJson;

    @Value("classpath:meta_api_definition.json")
    private Resource metaApiDefinitionJson;

    @Value("classpath:meta_api_definition.yaml")
    private Resource metaApiDefinitionYaml;

    @Mock
    private RestTemplate restTemplate;

    private WellKnownSchemaGateway schemaGateway;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        schemaGateway = new WellKnownSchemaGateway(restTemplate);
    }

    @Test
    public void shouldSuccessfullyRetrieveSchemaDiscovery() throws Exception {
        JsonNode mockedSchemaDiscovery = parseResource(metaApiSchemaDiscoveryJson);
        ResponseEntity<JsonNode> schemaResponse = new ResponseEntity<>(mockedSchemaDiscovery, HttpStatus.OK);
        doReturn(schemaResponse).when(restTemplate).exchange(
            eq("https://meta.api/.well-known/schema-discovery"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class));

        JsonNode schemaDiscovery = schemaGateway.retrieveSchemaDiscovery(metaApiKioApplication());

        assertThat(schemaDiscovery).isNotNull();
        assertThat(schemaDiscovery).isEqualTo(mockedSchemaDiscovery);
    }

    @Test
    public void shouldReturnNullIfNot2xxResponse() throws Exception {
        String mockedSchemaDiscovery = parseResource(metaApiSchemaDiscoveryJson).toString();
        ResponseEntity<String> schemaResponse = new ResponseEntity<>(mockedSchemaDiscovery, HttpStatus.NOT_FOUND);
        doReturn(schemaResponse).when(restTemplate).exchange(
            eq("https://meta.api/.well-known/schema-discovery"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class));

        JsonNode schemaDiscovery = schemaGateway.retrieveSchemaDiscovery(metaApiKioApplication());

        assertThat(schemaDiscovery).isNull();
    }

    @Test
    public void shouldRetrieveJsonApiDefinition() throws Exception {
        JsonNode mockedApiDefinition = parseResource(metaApiDefinitionJson);
        SchemaDiscovery mockedSchemaDiscovery = new SchemaDiscovery(parseResource(metaApiSchemaDiscoveryJson));

        ResponseEntity<String> definitionResponse = new ResponseEntity<>(mockedApiDefinition.toString(), HttpStatus.OK);
        doReturn(definitionResponse).when(restTemplate).exchange(
            eq("https://meta.api/swagger.json"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class));

        JsonNode apiDefinition = schemaGateway.retrieveApiDefinition(metaApiKioApplication(), mockedSchemaDiscovery);

        assertThat(apiDefinition).isNotNull();
        assertThat(apiDefinition).isEqualTo(mockedApiDefinition);
    }

    @Test
    public void shouldRetrieveYamlApiDefinition() throws Exception {
        JsonNode expectedApiDefinition = parseResource(metaApiDefinitionJson);
        SchemaDiscovery schemaDiscovery = new SchemaDiscovery(parseResource(metaApiSchemaDiscoveryJson));
        String minimalValidSwaggerYaml = parseResource(metaApiDefinitionYaml).toString();
        doReturn(new ResponseEntity<>(minimalValidSwaggerYaml, HttpStatus.OK)).when(restTemplate).exchange(
            eq("https://meta.api/swagger.json"),
            eq(HttpMethod.GET),
            anyObject(),
            eq(String.class));

        JsonNode apiDefinition = schemaGateway.retrieveApiDefinition(metaApiKioApplication(), schemaDiscovery);

        assertThat(apiDefinition).isNotNull();
        assertThat(apiDefinition).isEqualTo(expectedApiDefinition);
    }

}
