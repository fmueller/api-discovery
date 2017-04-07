package org.zalando.apidiscovery.storage.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.zalando.apidiscovery.storage.TestDataHelper.crawledMetaApi;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApiDefinitionProcessingServiceIntegrationTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private ApiDefinitionProcessingService apiService;

    @Before
    public void init() {
        apiRepository.deleteAll();
        applicationRepository.deleteAll();
    }

    @Test
    public void shouldBeAbleToAddFirstDefinitionTest() throws Exception {
        apiService.processCrawledApiDefinition(crawledMetaApi("1.0", "a"));

        List<ApiEntity> apis = apiRepository.findByApiName("meta-api");

        assertThat(apis.size()).isEqualTo(1);
        assertThat(apis.get(0).getDefinitionId()).isEqualTo(1);
    }

    @Test
    public void definitionIdShouldGrowTest() throws Exception {
        apiService.processCrawledApiDefinition(crawledMetaApi("1.0", "a"));
        apiService.processCrawledApiDefinition(crawledMetaApi("1.0", "b"));

        List<ApiEntity> apis = apiRepository.findByApiName("meta-api");

        assertThat(apis.size()).isEqualTo(2);
        assertThat(apis.get(0).getDefinitionId()).isEqualTo(1);
        assertThat(apis.get(1).getDefinitionId()).isEqualTo(2);
    }
}