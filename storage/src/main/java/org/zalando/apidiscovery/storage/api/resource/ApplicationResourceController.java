package org.zalando.apidiscovery.storage.api.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.apidiscovery.storage.api.domain.model.Application;
import org.zalando.apidiscovery.storage.api.domain.model.Applications;
import org.zalando.apidiscovery.storage.api.domain.logic.ApplicationService;

import java.util.List;

import static org.zalando.apidiscovery.storage.api.domain.logic.LinkBuilderUtil.buildLink;

@CrossOrigin
@RestController
@RequestMapping("/applications")
public class ApplicationResourceController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationResourceController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<Applications> getApplications(UriComponentsBuilder builder) {
        List<Application> applicationList = applicationService.getAllApplications();
        return ResponseEntity.ok(new Applications(updateApiDefinitionWithLinks(applicationList, builder)));
    }

    @GetMapping("/{application_name}")
    public ResponseEntity<Application> getApplication(@PathVariable("application_name") String applicationName, UriComponentsBuilder builder) {
        return applicationService.getApplication(applicationName)
            .map(dto -> ResponseEntity.ok(updateApiDefinitionWithLinks(dto, builder)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private List<Application> updateApiDefinitionWithLinks(List<Application> applicationList, UriComponentsBuilder builder) {
        applicationList.forEach(
            applicationDto -> updateApiDefinitionWithLinks(applicationDto, builder)
        );
        return applicationList;
    }

    private Application updateApiDefinitionWithLinks(Application application, UriComponentsBuilder builder) {
        application.getDefinitions()
            .forEach(deploymentLinkDto -> deploymentLinkDto.setHref(
                buildLink(builder.cloneBuilder(), deploymentLinkDto).toUriString()
            ));

        return application;
    }
}
