package org.zalando.apidiscovery.storage.api;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        List<Api> allApis = lifecycleState == null ? apiService.getAllApis() : apiService.getAllApis(lifecycleState);
        allApis.stream()
                .sorted(comparing(api -> api.getApiMetaData().getName()))
                .collect(toList());
        return ResponseEntity.ok(new ApiListDto(allApis));
    }

}
