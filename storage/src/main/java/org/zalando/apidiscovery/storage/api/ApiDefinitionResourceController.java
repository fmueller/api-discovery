package org.zalando.apidiscovery.storage.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<Void> postCrawledApiDefinition(@RequestBody CrawledApiDefinitionDto crawledAPIDefinitionDto)
            throws SwaggerParseException {

        apiDefinitionProcessingService.processCrawledApiDefinition(crawledAPIDefinitionDto);

        //TODO set location header
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(SwaggerParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleSwaggerParseException(SwaggerParseException e) {

    }
}
