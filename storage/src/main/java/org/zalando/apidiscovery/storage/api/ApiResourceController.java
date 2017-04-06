package org.zalando.apidiscovery.storage.api;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@CrossOrigin
@RestController
@RequestMapping("/apis")
public class ApiResourceController {

    private final ApiService apiService;

    @Autowired
    public ApiResourceController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping
    public ResponseEntity<ApiListDto> getApis(@RequestParam(value = "lifecycle_state", required = false) ApiLifecycleState lifecycleState) {
        List<ApiDto> apiList = loadApis(lifecycleState).stream()
            .sorted(comparing(api -> api.getApiMetaData().getName()))
            .collect(toList());
        return ResponseEntity.ok(new ApiListDto(apiList));
    }

    private List<ApiDto> loadApis(ApiLifecycleState lifecycleState) {
        return lifecycleState == null ? apiService.getAllApis() : apiService.getAllApis(lifecycleState);
    }

    @GetMapping("/{api_id}")
    public ResponseEntity<ApiDto> getApi(@PathVariable("api_id") String apiId, UriComponentsBuilder builder) {
        return apiService.getApi(apiId)
            .map(api -> ResponseEntity.ok(buildAndSetLinks(api, builder)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @GetMapping("/{api_id}/versions")
    public ResponseEntity<VersionListDto> getApiVersions(@PathVariable("api_id") String apiId,
                                                         @RequestParam(value = "lifecycle_state", required = false) ApiLifecycleState lifecycleState,
                                                         UriComponentsBuilder builder) {
        List<VersionsDto> versions = loadVersions(apiId, lifecycleState);
        buildAndSetApplicationLinks(versions, builder);
        return ResponseEntity.ok(new VersionListDto(versions));
    }

    private List<VersionsDto> loadVersions(String apiId, ApiLifecycleState lifecycleState) {
        return lifecycleState == null ? apiService.getVersionsForApi(apiId) : apiService.getVersionsForApi(apiId, lifecycleState);
    }

    @GetMapping("/{api_id}/versions/{version_id}")
    public ResponseEntity<VersionsDto> getApiVersions(@PathVariable("api_id") String apiId,
                                                      @PathVariable("version_id") String version,
                                                      UriComponentsBuilder builder) {
        return apiService.getVersion(apiId, version)
            .map(versionsDto -> ResponseEntity.ok(buildAndSetApplicationLinks(versionsDto, builder)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private ApiDto buildAndSetLinks(ApiDto api, UriComponentsBuilder builder) {
        buildAndSetApplicationLinks(api.getVersions(), builder);

        api.getApplications()
            .forEach(applicationDto -> applicationDto.getDefinitions()
                .forEach(deploymentLinkDto -> deploymentLinkDto.setHref(
                    builder.cloneBuilder()
                        .path(deploymentLinkDto.getLinkBuilder().buildLink())
                        .toUriString()))
            );

        return api;
    }

    private List<VersionsDto> buildAndSetApplicationLinks(List<VersionsDto> versions, UriComponentsBuilder builder) {
        versions.forEach(versionsDto -> buildAndSetApplicationLinks(versionsDto, builder));
        return versions;
    }

    private VersionsDto buildAndSetApplicationLinks(VersionsDto version, UriComponentsBuilder builder) {
        version.getDefinitions()
            .forEach(apiDefinitionDto -> apiDefinitionDto.getApplications()
                .forEach(deploymentLinkDto -> deploymentLinkDto.setHref(
                    builder.cloneBuilder()
                        .path(deploymentLinkDto.getLinkBuilder().buildLink())
                        .toUriString())));

        return version;
    }

}
