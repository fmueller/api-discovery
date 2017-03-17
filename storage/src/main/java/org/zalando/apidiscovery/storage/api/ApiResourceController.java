package org.zalando.apidiscovery.storage.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@CrossOrigin
@RestController
@RequestMapping("/apis")
public class ApiResourceController {

    @Autowired
    private ApiService apiService;

    @GetMapping
    public ResponseEntity<ApiListDto> getApis(@RequestParam(value = "lifecycle_state", required = false) ApiLifecycleState lifecycleState) {
        List<Api> allApis = lifecycleState == null ? apiService.getAllApis() : apiService.getAllApis(lifecycleState);
        allApis.stream()
                .sorted(comparing(api -> api.getApiMetaData().getName()))
                .collect(toList());
        return ResponseEntity.ok(new ApiListDto(allApis));
    }

}
