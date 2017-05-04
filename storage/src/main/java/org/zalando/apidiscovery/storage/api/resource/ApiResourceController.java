package org.zalando.apidiscovery.storage.api.resource;

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
import org.zalando.apidiscovery.storage.api.service.dto.ApiDefinitionDto;
import org.zalando.apidiscovery.storage.api.service.dto.ApiDto;
import org.zalando.apidiscovery.storage.api.domain.ApiLifecycleState;
import org.zalando.apidiscovery.storage.api.service.dto.ApiListDto;
import org.zalando.apidiscovery.storage.api.service.dto.DeploymentDto;
import org.zalando.apidiscovery.storage.api.service.dto.DeploymentsDto;
import org.zalando.apidiscovery.storage.api.service.dto.VersionListDto;
import org.zalando.apidiscovery.storage.api.service.dto.VersionsDto;
import org.zalando.apidiscovery.storage.api.service.ApiService;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.zalando.apidiscovery.storage.api.resource.LinkBuilderUtil.buildLink;

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
            .map(api -> ResponseEntity.ok(buildLinks(api, builder)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{api_id}/versions")
    public ResponseEntity<VersionListDto> getApiVersions(@PathVariable("api_id") String apiId,
                                                         @RequestParam(value = "lifecycle_state", required = false) ApiLifecycleState lifecycleState,
                                                         UriComponentsBuilder builder) {
        List<VersionsDto> versions = loadVersions(apiId, lifecycleState);
        return ResponseEntity.ok(new VersionListDto(updateApiDefinitionWithLinks(versions, builder)));
    }

    private List<VersionsDto> loadVersions(String apiId, ApiLifecycleState lifecycleState) {
        return lifecycleState == null ? apiService.getVersionsForApi(apiId) : apiService.getVersionsForApi(apiId, lifecycleState);
    }

    @GetMapping("/{api_id}/versions/{version_id}")
    public ResponseEntity<VersionsDto> getApiVersion(@PathVariable("api_id") String apiId,
                                                     @PathVariable("version_id") String versionId,
                                                     UriComponentsBuilder builder) {
        return apiService.getVersion(apiId, versionId)
            .map(versionsDto -> ResponseEntity.ok(updateApiDefinitionWithLinks(versionsDto, builder)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{api_id}/versions/{version_id}/definitions/{definition_id}")
    public ResponseEntity<ApiDefinitionDto> getApiDefinition(@PathVariable("api_id") String apiId,
                                                             @PathVariable("version_id") String versionId,
                                                             @PathVariable("definition_id") String definitionId,
                                                             UriComponentsBuilder builder) {
        return apiService.getApiDefinitionDto(apiId, versionId, definitionId)
            .map(definitionDto -> ResponseEntity.ok(updateApiDefinitionWithLinks(definitionDto, builder)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{api_id}/deployments")
    public ResponseEntity<DeploymentsDto> getApiDeployments(@PathVariable("api_id") String apiId,
                                                            UriComponentsBuilder builder) {
        List<DeploymentDto> deploymentsForApi = apiService.getDeploymentsForApi(apiId);
        return ResponseEntity.ok(new DeploymentsDto(updateLinks(deploymentsForApi, builder)));

    }

    private List<DeploymentDto> updateLinks(List<DeploymentDto> deploymentDtoList, UriComponentsBuilder builder) {
        deploymentDtoList.forEach(
            deploymentDto -> deploymentDto.buildLinks(builder)
        );
        return deploymentDtoList;
    }

    private ApiDto buildLinks(ApiDto api, UriComponentsBuilder builder) {
        updateApiDefinitionWithLinks(api.getVersions(), builder);

        api.getApplications()
            .forEach(applicationDto -> applicationDto.getDefinitions()
                .forEach(deploymentLinkDto -> deploymentLinkDto.setHref(
                    buildLink(builder.cloneBuilder(), deploymentLinkDto).toUriString()
                )));

        return api;
    }

    private List<VersionsDto> updateApiDefinitionWithLinks(List<VersionsDto> versions, UriComponentsBuilder builder) {
        versions.forEach(versionsDto -> updateApiDefinitionWithLinks(versionsDto, builder));

        return versions;
    }

    private VersionsDto updateApiDefinitionWithLinks(VersionsDto version, UriComponentsBuilder builder) {
        version.getDefinitions()
            .forEach(apiDefinitionDto -> updateApiDefinitionWithLinks(apiDefinitionDto, builder));

        return version;
    }

    private ApiDefinitionDto updateApiDefinitionWithLinks(ApiDefinitionDto apiDefinitionDto, UriComponentsBuilder builder) {
        apiDefinitionDto.getApplications()
            .forEach(deploymentLinkDto -> deploymentLinkDto.setHref(
                buildLink(builder.cloneBuilder(), deploymentLinkDto).toUriString()
            ));

        return apiDefinitionDto;
    }

}
