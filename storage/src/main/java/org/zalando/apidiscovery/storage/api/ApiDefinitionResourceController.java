package org.zalando.apidiscovery.storage.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api-definitions")
public class ApiDefinitionResourceController {

    private ApiDefinitionManager apiDefinitionManager;

    @Autowired
    public ApiDefinitionResourceController(ApiDefinitionManager apiDefinitionManager) {
        this.apiDefinitionManager = apiDefinitionManager;
    }

    @PostMapping
    public ResponseEntity<Void> postCrawledApiDefinition(@RequestBody CrawledApiDefinitionDto crawledAPIDefinitionDto) {
        apiDefinitionManager.processCrawledApiDefinition(crawledAPIDefinitionDto);

        //TODO set location header
        return ResponseEntity.ok().build();
    }

}
