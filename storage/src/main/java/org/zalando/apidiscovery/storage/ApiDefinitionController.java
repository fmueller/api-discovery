package org.zalando.apidiscovery.storage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@CrossOrigin
@RestController
@RequestMapping(value = "/apps")
class ApiDefinitionController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDefinitionController.class);

    private final ApiDefinitionRepository repository;
    private final ApiDefinitionService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    ApiDefinitionController(ApiDefinitionRepository repository, ApiDefinitionService service) {
        this.repository = repository;
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ApiDefinitionListDto> listAll(@RequestParam(value = "lifecycle_state", required = false) String lifecycleState) {
        List<ApiDefinitionListDto> definitions = new LinkedList<>();

        if (lifecycleState == null) {
            repository.findAll().forEach(d -> definitions.add(new ApiDefinitionListDto(d)));
            LOG.info("Retrieved all api definitions");
        } else {
            repository.findByLifecycleState(lifecycleState.toUpperCase()).forEach(d -> definitions.add(new ApiDefinitionListDto(d)));
            LOG.info("Retrieved all api definitions with lifecycle state {}", lifecycleState);
        }

        return definitions;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{applicationId}")
    public ResponseEntity findOne(@PathVariable String applicationId) throws IOException {
        ApiDefinition apiDefinition = repository.findOne(applicationId);
        if (apiDefinition == null) {
            LOG.info("Not found api definition for {}", applicationId);
            return ResponseEntity.notFound().build();
        }

        LOG.info("Retrieve api definition for {}", applicationId);
        return ResponseEntity.ok(addSchemeToServiceUrl(stripOffTrailingSlashesInServiceUrl(apiDefinition)));
    }

    private ApiDefinition addSchemeToServiceUrl(ApiDefinition apiDefinition) throws IOException {
        final String serviceUrl = apiDefinition.getServiceUrl();
        if (serviceUrl != null) {
            final JsonNode definition = mapper.readTree(apiDefinition.getDefinition());
            if (definition.has("schemes")) {
                if (definition.get("schemes").isArray()) {
                    final ArrayNode schemes = (ArrayNode) definition.get("schemes");
                    if (schemes.size() >= 1) {
                        apiDefinition.setServiceUrl(schemes.get(0).asText() + "://" + serviceUrl);
                    }
                } else {
                    apiDefinition.setServiceUrl(definition.get("schemes").asText() + "://" + serviceUrl);
                }
            }
        }
        return apiDefinition;
    }

    private ApiDefinition stripOffTrailingSlashesInServiceUrl(ApiDefinition apiDefinition) {
        final String serviceUrl = apiDefinition.getServiceUrl();
        if (serviceUrl != null && serviceUrl.endsWith("/")) {
            apiDefinition.setServiceUrl(serviceUrl.substring(0, serviceUrl.length() - 1));
        }
        return apiDefinition;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{applicationId}/definition")
    public ResponseEntity findDefinition(@PathVariable String applicationId) throws IOException {
        ApiDefinition apiDefinition = repository.findOne(applicationId);
        if (apiDefinition == null) {
            LOG.info("Not found json of api definition for {}", applicationId);
            return ResponseEntity.notFound().build();
        }

        LOG.info("Retrieve json of api definition for {}", applicationId);
        return ResponseEntity.ok(mapper.readTree(apiDefinition.getDefinition()));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{applicationId}")
    public ResponseEntity<Void> persistOne(@RequestBody ApiDefinition apiDefinition, @PathVariable String applicationId) {
        apiDefinition.setApplicationId(applicationId);
        service.persistWithMetadata(apiDefinition);
        LOG.info("Persisted api definition for {} with status {}", applicationId, apiDefinition.getStatus());
        return ResponseEntity.created(UriComponentsBuilder.fromPath("/apps/" + applicationId).build().toUri()).build();
    }
}
