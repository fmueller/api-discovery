package org.zalando.apidiscovery.storage.api;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@CrossOrigin
@RestController
@RequestMapping("/applications")
public class ApplicationResourceController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<ApplicationListDto> getApplications(UriComponentsBuilder builder) {
        List<ApplicationDto> applicationDtoList = applicationService.getAllApplications();
        return ResponseEntity.ok(new ApplicationListDto(updateApiDefinitionWithLinks(applicationDtoList, builder)));
    }

    @GetMapping("/{application_name}")
    public ResponseEntity<ApplicationDto> getApplication(@PathVariable("application_name") String applicationName, UriComponentsBuilder builder) {
        Optional<ApplicationDto> applicationDto = applicationService.getApplication(applicationName);
        return applicationDto.map(dto -> ResponseEntity.ok(updateApiDefinitionWithLinks(dto, builder)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private List<ApplicationDto> updateApiDefinitionWithLinks(List<ApplicationDto> applicationDtoList, UriComponentsBuilder builder) {
        applicationDtoList.forEach(
            applicationDto -> updateApiDefinitionWithLinks(applicationDto, builder)
        );
        return applicationDtoList;
    }

    private ApplicationDto updateApiDefinitionWithLinks(ApplicationDto applicationDto, UriComponentsBuilder builder) {
        applicationDto.getDefinitions().
            forEach(deploymentLinkDto -> deploymentLinkDto.setHref(
                builder.cloneBuilder()
                    .path(deploymentLinkDto.getLinkBuilder().buildLink())
                    .toUriString()
            ));

        return applicationDto;
    }
}
