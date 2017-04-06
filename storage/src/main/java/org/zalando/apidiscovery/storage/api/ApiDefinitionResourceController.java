package org.zalando.apidiscovery.storage.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.apidiscovery.storage.utils.SwaggerParseException;

import java.net.URI;

import static java.lang.String.valueOf;

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
    public ResponseEntity<Void> postCrawledApiDefinition(@RequestBody CrawledApiDefinitionDto crawledAPIDefinitionDto,
                                                         UriComponentsBuilder builder) throws SwaggerParseException {
        final ApiEntity api = apiDefinitionProcessingService.processCrawledApiDefinition(crawledAPIDefinitionDto);

        final DefinitionDeploymentLinkBuilder linkBuilder = new DefinitionDeploymentLinkBuilder(
                api.getApiName(),
                api.getApiVersion(),
                valueOf(api.getId()));

        final URI location = builder.path(linkBuilder.buildLink()).build().encode().toUri();
        return ResponseEntity.created(location).build();
    }

    @ExceptionHandler(SwaggerParseException.class)
    public ResponseEntity<Void> handleSwaggerParseException(SwaggerParseException e) {
        return ResponseEntity.badRequest().build();
    }
}
