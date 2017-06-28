package org.zalando.apidiscovery.storage.domain.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.apidiscovery.storage.domain.model.DiscoveredApiDefinition;
import org.zalando.apidiscovery.storage.repository.ApiEntity;
import org.zalando.apidiscovery.storage.repository.ApiRepository;
import org.zalando.apidiscovery.storage.repository.ApplicationRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.TestDataHelper.discoveredMetaApi;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
public class ApiDefinitionProcessingServiceComponentTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private EntityManager entityManager;

    private ApiDefinitionProcessingService apiService;

    @Before
    public void setUp() throws Exception {
        apiService = new ApiDefinitionProcessingService(applicationRepository, apiRepository, entityManager);
    }

    @Test
    public void shouldBeAbleToAddFirstDefinition() throws Exception {
        assertThat(apiRepository.findByApiName("meta-api").size()).isEqualTo(0);

        final DiscoveredApiDefinition apiDef = discoveredMetaApi("1.0", "a");
        apiService.processDiscoveredApiDefinition(apiDef);

        List<ApiEntity> apis = apiRepository.findByApiName("meta-api");
        assertThat(apis.size()).isEqualTo(1);
        final ApiEntity persistedApi = apis.get(0);
        assertThat(persistedApi.getApiVersion()).isEqualTo(apiDef.getVersion());
        assertThat(persistedApi.getDefinition()).isEqualTo(apiDef.getDefinition());
        assertThat(persistedApi.getCreated()).isNotNull();
        assertThat(persistedApi.getApiName()).isEqualTo(apiDef.getApiName());
        assertThat(persistedApi.getDefinitionHash()).isEqualTo("27fb1e45ce72b8ab49ef982fb1b38482fcce0ae6ac94fe461f1b0ac2f3fad675");
        assertThat(persistedApi.getDefinitionType()).isEqualTo(apiDef.getType());
        assertThat(persistedApi.getDefinitionId()).isEqualTo(1);
    }

    @Test
    public void definitionIdShouldGrow() throws Exception {
        assertThat(apiRepository.findByApiName("meta-api").size()).isEqualTo(0);

        final DiscoveredApiDefinition apiDef = discoveredMetaApi("1.0", "diff-a");
        final DiscoveredApiDefinition slightlyDifferentApiDef = discoveredMetaApi("1.0", "diff-b");
        apiService.processDiscoveredApiDefinition(apiDef);
        apiService.processDiscoveredApiDefinition(slightlyDifferentApiDef);

        List<ApiEntity> apis = apiRepository.findByApiName("meta-api");
        assertThat(apis.size()).isEqualTo(2);
        assertThat(apis.get(0).getDefinitionId()).isEqualTo(1);
        assertThat(apis.get(0).getDefinition()).isEqualTo(apiDef.getDefinition());
        assertThat(apis.get(1).getDefinitionId()).isEqualTo(2);
        assertThat(apis.get(1).getDefinition()).isEqualTo(slightlyDifferentApiDef.getDefinition());
    }

    @Test
    @Transactional
    public void shouldBeAbleToHandleUniqueConstraintViolation() throws Exception {
        final ApiDefinitionProcessingService service = new ApiDefinitionProcessingService(applicationRepository, apiRepository, entityManager) {
            private int counter = 0;

            @Override
            protected int nextDefinitionId(DiscoveredApiDefinition discoveredApiDefinition) {
                return counter++ < 2 ? 1 : 2;
            }
        };

        service.processDiscoveredApiDefinition(discoveredMetaApi("1.0", "diff-a"));
        service.processDiscoveredApiDefinition(discoveredMetaApi("1.0", "diff-b"));

        List<ApiEntity> apis = apiRepository.findByApiName("meta-api");

        assertThat(apis.size()).isEqualTo(2);
        assertThat(apis.get(0).getDefinitionId()).isEqualTo(1);
        assertThat(apis.get(1).getDefinitionId()).isEqualTo(2);
    }
}