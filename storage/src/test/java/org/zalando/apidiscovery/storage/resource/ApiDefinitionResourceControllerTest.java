package org.zalando.apidiscovery.storage.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.zalando.apidiscovery.storage.domain.model.DiscoveredApiDefinition;
import org.zalando.apidiscovery.storage.domain.model.DiscoveredApiDefinitionState;
import org.zalando.apidiscovery.storage.domain.service.ApiDefinitionProcessingService;
import org.zalando.apidiscovery.storage.repository.ApiEntity;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.zalando.apidiscovery.storage.domain.model.DiscoveredApiDefinitionState.SUCCESSFUL;

@RunWith(SpringRunner.class)
@WebMvcTest(ApiDefinitionResourceController.class)
public class ApiDefinitionResourceControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ApiDefinitionProcessingService apiDefinitionService;

    @Test
    public void shouldReturnHttpCodeCreatedAndLocationHeader() throws Exception {
        final ApiEntity api = ApiEntity.builder()
            .apiName("meta-api")
            .apiVersion("1.0")
            .definitionId(1)
            .build();

        given(apiDefinitionService.processDiscoveredApiDefinition(any(DiscoveredApiDefinition.class)))
            .willReturn(api);

        DiscoveredApiDefinition apiDefinition = createDiscoveredApiDefinition("api-service", SUCCESSFUL);

        mvc.perform(
            post("/api-definitions")
                .contentType(APPLICATION_JSON)
                .content(toJson(apiDefinition)))
            .andExpect(status().isCreated())
            .andExpect(header().string("location", "http://localhost/apis/meta-api/versions/1.0/definitions/1"));
    }

    @Test
    public void shouldReturnBadRequestForBlankApplicationName() throws Exception {
        DiscoveredApiDefinition apiDefinition = createDiscoveredApiDefinition(" ", SUCCESSFUL);
        mvc.perform(
            post("/api-definitions")
                .contentType(APPLICATION_JSON)
                .content(toJson(apiDefinition)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestForInvalidStatus() throws Exception {
        DiscoveredApiDefinition apiDefinition = createDiscoveredApiDefinition("api-service", null);
        mvc.perform(
            post("/api-definitions")
                .contentType(APPLICATION_JSON)
                .content(toJson(apiDefinition)))
            .andExpect(status().isBadRequest());
    }

    private String toJson(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    private DiscoveredApiDefinition createDiscoveredApiDefinition(String applicationName, DiscoveredApiDefinitionState status) {
        return DiscoveredApiDefinition.builder()
            .apiName("meta-api")
            .appName(applicationName)
            .definition("{\\\"info\\\":{\\\"title\\\":\\\"Meta API\\\",\\\"version\\\":\\\"v1\\\"}}")
            .serviceUrl("http:localhost:8080/")
            .type("swagger")
            .version("v1")
            .status(status)
            .build();
    }

}
