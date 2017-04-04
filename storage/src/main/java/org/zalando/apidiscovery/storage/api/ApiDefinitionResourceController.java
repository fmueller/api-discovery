package org.zalando.apidiscovery.storage.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api-definitions")
public class ApiDefinitionResourceController {

    private CrawlerService crawlerService;

    @Autowired
    public ApiDefinitionResourceController(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @PostMapping
    public ResponseEntity<?> postCrawledApiDefinition(@RequestBody CrawledApiDefinitionDto crawledAPIDefinitionDto) {
        crawlerService.processCrawledApiDefinition(crawledAPIDefinitionDto);

        //TODO set location header
        return ResponseEntity.ok().build();
    }

}
