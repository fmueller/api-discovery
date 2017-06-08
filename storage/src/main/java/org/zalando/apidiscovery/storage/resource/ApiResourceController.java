package org.zalando.apidiscovery.storage.resource;

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
import org.zalando.apidiscovery.storage.domain.model.ApiDefinition;
import org.zalando.apidiscovery.storage.domain.model.Api;
import org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState;
import org.zalando.apidiscovery.storage.domain.model.Apis;
import org.zalando.apidiscovery.storage.domain.model.Deployment;
import org.zalando.apidiscovery.storage.domain.model.Deployments;
import org.zalando.apidiscovery.storage.domain.model.VersionList;
import org.zalando.apidiscovery.storage.domain.model.Versions;
import org.zalando.apidiscovery.storage.domain.service.ApiService;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.zalando.apidiscovery.storage.domain.service.LinkBuilderUtil.buildLink;

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
    public ResponseEntity<Apis> getApis(@RequestParam(value = "lifecycle_state", required = false) ApiLifecycleState lifecycleState) {
        List<Api.ApiMetaData> apiList = loadApis(lifecycleState).stream()
            .map(Api::getApiMetaData)
            .sorted(comparing(Api.ApiMetaData::getId))
            .collect(toList());
        return ResponseEntity.ok(new Apis(apiList));
    }

    private List<Api> loadApis(ApiLifecycleState lifecycleState) {
        return lifecycleState == null ? apiService.getAllApis() : apiService.getAllApis(lifecycleState);
    }

    @GetMapping("/{api_id}")
    public ResponseEntity<Api> getApi(@PathVariable("api_id") String apiId, UriComponentsBuilder builder) {
        return apiService.getApi(apiId)
            .map(api -> ResponseEntity.ok(buildLinks(api, builder)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{api_id}/versions")
    public ResponseEntity<VersionList> getApiVersions(@PathVariable("api_id") String apiId,
                                                      @RequestParam(value = "lifecycle_state", required = false) ApiLifecycleState lifecycleState,
                                                      UriComponentsBuilder builder) {
        List<Versions> versions = loadVersions(apiId, lifecycleState);
        return ResponseEntity.ok(new VersionList(updateApiDefinitionWithLinks(versions, builder)));
    }

    private List<Versions> loadVersions(String apiId, ApiLifecycleState lifecycleState) {
        return lifecycleState == null ? apiService.getVersionsForApi(apiId) : apiService.getVersionsForApi(apiId, lifecycleState);
    }

    @GetMapping("/{api_id}/versions/{version_id}")
    public ResponseEntity<Versions> getApiVersion(@PathVariable("api_id") String apiId,
                                                  @PathVariable("version_id") String versionId,
                                                  UriComponentsBuilder builder) {
        return apiService.getVersion(apiId, versionId)
            .map(versionsDto -> ResponseEntity.ok(updateApiDefinitionWithLinks(versionsDto, builder)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{api_id}/versions/{version_id}/definitions/{definition_id}")
    public ResponseEntity<ApiDefinition> getApiDefinition(@PathVariable("api_id") String apiId,
                                                          @PathVariable("version_id") String versionId,
                                                          @PathVariable("definition_id") String definitionId,
                                                          UriComponentsBuilder builder) {
        return apiService.getApiDefinitionDto(apiId, versionId, definitionId)
            .map(definitionDto -> ResponseEntity.ok(updateApiDefinitionWithLinks(definitionDto, builder)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{api_id}/deployments")
    public ResponseEntity<Deployments> getApiDeployments(@PathVariable("api_id") String apiId,
                                                         UriComponentsBuilder builder) {
        List<Deployment> deploymentsForApi = apiService.getDeploymentsForApi(apiId);
        return ResponseEntity.ok(new Deployments(updateLinks(deploymentsForApi, builder)));

    }

    private List<Deployment> updateLinks(List<Deployment> deploymentList, UriComponentsBuilder builder) {
        deploymentList.forEach(
            deployment -> deployment.buildLinks(builder)
        );
        return deploymentList;
    }

    private Api buildLinks(Api api, UriComponentsBuilder builder) {
        updateApiDefinitionWithLinks(api.getVersions(), builder);

        api.getApplications()
            .forEach(applicationDto -> applicationDto.getDefinitions()
                .forEach(deploymentLinkDto -> deploymentLinkDto.setHref(
                    buildLink(builder.cloneBuilder(), deploymentLinkDto).toUriString()
                )));

        return api;
    }

    private List<Versions> updateApiDefinitionWithLinks(List<Versions> versions, UriComponentsBuilder builder) {
        versions.forEach(versionsDto -> updateApiDefinitionWithLinks(versionsDto, builder));

        return versions;
    }

    private Versions updateApiDefinitionWithLinks(Versions version, UriComponentsBuilder builder) {
        version.getDefinitions()
            .forEach(apiDefinitionDto -> updateApiDefinitionWithLinks(apiDefinitionDto, builder));

        return version;
    }

    private ApiDefinition updateApiDefinitionWithLinks(ApiDefinition apiDefinition, UriComponentsBuilder builder) {
        apiDefinition.getApplications()
            .forEach(deploymentLinkDto -> deploymentLinkDto.setHref(
                buildLink(builder.cloneBuilder(), deploymentLinkDto).toUriString()
            ));

        return apiDefinition;
    }

}
