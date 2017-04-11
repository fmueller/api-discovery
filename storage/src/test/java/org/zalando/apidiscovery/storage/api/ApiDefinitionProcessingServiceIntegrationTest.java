package org.zalando.apidiscovery.storage.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.zalando.apidiscovery.storage.TestDataHelper.discoveredMetaApi;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
public class ApiDefinitionProcessingServiceIntegrationTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private ApiDefinitionProcessingService apiService;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldBeAbleToAddFirstDefinition() throws Exception {
        apiService.processDiscoveredApiDefinition(discoveredMetaApi("1.0", "a"));

        List<ApiEntity> apis = apiRepository.findByApiName("meta-api");

        assertThat(apis.size()).isEqualTo(1);
        assertThat(apis.get(0).getDefinitionId()).isEqualTo(1);
    }

    @Test
    public void definitionIdShouldGrow() throws Exception {
        apiService.processDiscoveredApiDefinition(discoveredMetaApi("1.0", "a"));
        apiService.processDiscoveredApiDefinition(discoveredMetaApi("1.0", "b"));

        List<ApiEntity> apis = apiRepository.findByApiName("meta-api");

        assertThat(apis.size()).isEqualTo(2);
        assertThat(apis.get(0).getDefinitionId()).isEqualTo(1);
        assertThat(apis.get(1).getDefinitionId()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void shouldBeAbleToHandleUniqueConstraintViolation() throws Exception {
        ApiDefinitionProcessingService service = new ApiDefinitionProcessingService(applicationRepository, apiRepository, entityManager) {
            private int counter = 0;

            @Override
            protected int nextDefinitionId(DiscoveredApiDefinition discoveredApiDefinition) {
                return counter++ < 2 ? 1 : 2;
            }
        };

        service.processDiscoveredApiDefinition(discoveredMetaApi("1.0", "a"));
        service.processDiscoveredApiDefinition(discoveredMetaApi("1.0", "b"));

        List<ApiEntity> apis = apiRepository.findByApiName("meta-api");

        assertThat(apis.size()).isEqualTo(2);
        assertThat(apis.get(0).getDefinitionId()).isEqualTo(1);
        assertThat(apis.get(1).getDefinitionId()).isEqualTo(2);
    }
}