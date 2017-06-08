package org.zalando.apidiscovery.storage.domain.model;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState.ACTIVE;

@RunWith(SpringRunner.class)
@JsonTest
public class ApiJsonTest {

    @Autowired
    private JacksonTester<Api> json;

    @Test
    public void shouldOmitFieldsWithNullValues() throws Exception {
        Api api = new Api("test-api", ACTIVE);

        assertThat(this.json.write(api))
            .doesNotHaveJsonPathValue("$.versions")
            .doesNotHaveJsonPathValue("$.applications")
            .doesNotHaveEmptyJsonPathValue("$.apiMetaData.id")
            .doesNotHaveEmptyJsonPathValue("$.apiMetaData.lifecycleState");
    }
}
