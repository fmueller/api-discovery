package org.zalando.apidiscovery.storage.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/apis")
public class ApiResourceController {

    @Autowired
    private ApiService apiService;

    @GetMapping
    public ResponseEntity<ApiListDto> getApis() {
        List<Api> allApis = apiService.getAllApis();
        return ResponseEntity.ok(new ApiListDto(allApis));
    }

}
