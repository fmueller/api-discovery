package org.zalando.apidiscovery.storage.api;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.apidiscovery.storage.utils.SwaggerParseException;

@CrossOrigin
@RestController
@RequestMapping("/api-definitions")
public class ApiDefinitionResourceController {

    private ApiDefinitionProcessingService apiDefinitionProcessingService;

    @Autowired
    public ApiDefinitionResourceController(ApiDefinitionProcessingService apiDefinitionProcessingService) {
        this.apiDefinitionProcessingService = apiDefinitionProcessingService;
    }

    @PostMapping
    public ResponseEntity<Void> postDiscoveredApiDefinition(@RequestBody DiscoveredApiDefinition discoveredAPIDefinition, UriComponentsBuilder builder)
            throws SwaggerParseException {
        final ApiEntity api = apiDefinitionProcessingService.processDiscoveredApiDefinition(discoveredAPIDefinition);
        final LinkBuilder linkBuilder = new DefinitionDeploymentLinkBuilder(api);
        final URI location = builder.path(linkBuilder.buildLink()).build().encode().toUri();
        return ResponseEntity.created(location).build();
    }

}
