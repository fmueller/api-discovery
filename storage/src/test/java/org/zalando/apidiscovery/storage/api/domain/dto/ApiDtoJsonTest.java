package org.zalando.apidiscovery.storage.api.domain.dto;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.api.domain.ApiLifecycleState.ACTIVE;

@RunWith(SpringRunner.class)
@JsonTest
public class ApiDtoJsonTest {

    @Autowired
    private JacksonTester<ApiDto> json;

    @Test
    public void shouldOmitFieldsWithNullValues() throws Exception {
        ApiDto apiDto = new ApiDto("test-api", ACTIVE);

        assertThat(this.json.write(apiDto))
            .doesNotHaveJsonPathValue("$.versions")
            .doesNotHaveJsonPathValue("$.applications")
            .doesNotHaveEmptyJsonPathValue("$.apiMetaData.name")
            .doesNotHaveEmptyJsonPathValue("$.apiMetaData.lifecycleState");
    }
}