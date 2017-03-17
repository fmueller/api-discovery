package org.zalando.apidiscovery.storage;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.apidiscovery.storage.api.ApiEntity;
import org.zalando.apidiscovery.storage.api.ApiLifecycleState;
import org.zalando.apidiscovery.storage.api.ApiRepository;
import org.zalando.apidiscovery.storage.api.ApplicationEntity;
import org.zalando.apidiscovery.storage.api.ApplicationRepository;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiRepositoryTest {

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Before
    public void cleanDatabase() {
        applicationRepository.deleteAll();
    }

    @Test
    public void shouldReturnAllApis() throws Exception {
        ApplicationEntity app1 = ApplicationEntity.builder().name("testApp")
                .lastCrawled(now())
                .created(now())
                .crawledState("SUCCESSFUL")
                .build();

        ApiEntity testAPi100 = ApiEntity.builder().apiName("testAPi")
                .apiVersion("1.0.0")
                .lifecycleState(ApiLifecycleState.INACTIVE)
                .application(app1)
                .build();
        ApiEntity testAPi101 = ApiEntity.builder().apiName("testAPi")
                .apiVersion("1.0.1")
                .lifecycleState(ApiLifecycleState.ACTIVE)
                .application(app1)
                .build();

        app1.setApiEntities(asList(testAPi100, testAPi101));
        applicationRepository.save(app1);

        List<ApiEntity> allApiEntitiesList = apiRepository.findAll();
        assertThat(allApiEntitiesList)
                .isNotNull()
                .hasSize(2);
    }
}
