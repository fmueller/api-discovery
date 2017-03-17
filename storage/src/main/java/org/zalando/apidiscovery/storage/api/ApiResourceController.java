package org.zalando.apidiscovery.storage.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@CrossOrigin
@RestController
@RequestMapping("/apis")
public class ApiResourceController {

    @Autowired
    private ApiRepository apiRepositroy;

    @GetMapping
    public ApiListDto getApis() {

        return ApiListDto.builder().apis(new ArrayList<>()).build();
    }

}
