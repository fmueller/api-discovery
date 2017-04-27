package org.zalando.apidiscovery.storage.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.zalando.apidiscovery.storage.api.LinkBuilderUtil.buildLink;

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
    public ResponseEntity<ApplicationsDto> getApplications(UriComponentsBuilder builder) {
        List<ApplicationDto> applicationDtoList = applicationService.getAllApplications();
        return ResponseEntity.ok(new ApplicationsDto(updateApiDefinitionWithLinks(applicationDtoList, builder)));
    }

    @GetMapping("/{application_name}")
    public ResponseEntity<ApplicationDto> getApplication(@PathVariable("application_name") String applicationName, UriComponentsBuilder builder) {
        return applicationService.getApplication(applicationName)
            .map(dto -> ResponseEntity.ok(updateApiDefinitionWithLinks(dto, builder)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private List<ApplicationDto> updateApiDefinitionWithLinks(List<ApplicationDto> applicationDtoList, UriComponentsBuilder builder) {
        applicationDtoList.forEach(
            applicationDto -> updateApiDefinitionWithLinks(applicationDto, builder)
        );
        return applicationDtoList;
    }

    private ApplicationDto updateApiDefinitionWithLinks(ApplicationDto applicationDto, UriComponentsBuilder builder) {
        applicationDto.getDefinitions()
            .forEach(deploymentLinkDto -> deploymentLinkDto.setHref(
                buildLink(builder.cloneBuilder(), deploymentLinkDto).toUriString()
            ));

        return applicationDto;
    }
}
