package org.zalando.apidiscovery.storage.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.zalando.apidiscovery.storage.TestDataHelper.crawledUberApiDefinitionRequestBody;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class APIDefinitionRestIntegrationTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityManager entityManager;

    @Before
    public void init() {
        apiRepository.deleteAll();
        applicationRepository.deleteAll();
    }

    @Test
    public void shouldCreateNewApplicationAndNewDeploymentTest() throws Exception {
        restTemplate.exchange("/api-definitions", HttpMethod.POST, crawledUberAPIDefinitionRequest(), Void.class);

        final List<ApiEntity> apis = apiRepository.findByApiNameAndApiVersion("uber-api", "1.0.0");
        assertThat(apis.size()).isEqualTo(1);

        final Optional<ApplicationEntity> app = applicationRepository.findOneByName("uber.api");
        assertThat(app).isPresent();

        final ApiDeploymentEntity deployment = entityManager
                .find(ApiDeploymentEntity.class, new ApiDeploymentEntity(apis.get(0), app.get()));
        assertThat(deployment.getLifecycleState()).isEqualTo(ApiLifecycleState.ACTIVE);
    }

    @Test
    public void shouldCreateNewDeploymentOnlyIfApplicationAlreadyExists() throws Exception {
        final ApplicationEntity app = ApplicationEntity.builder()
                .name("uber.api")
                .build();
        applicationRepository.saveAndFlush(app);

        restTemplate.exchange("/api-definitions", HttpMethod.POST, crawledUberAPIDefinitionRequest(), Void.class);
        final List<ApiEntity> apis = apiRepository.findByApiNameAndApiVersion("uber-api", "1.0.0");
        assertThat(apis.size()).isEqualTo(1);
    }

    private HttpEntity<String> crawledUberAPIDefinitionRequest() throws IOException, URISyntaxException {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", APPLICATION_JSON_UTF8_VALUE);
        return new HttpEntity<>(crawledUberApiDefinitionRequestBody(), headers);
    }
}