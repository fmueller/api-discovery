package org.zalando.apidiscovery.storage;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.apidiscovery.storage.domain.service.ApiDefinitionProcessingService;
import org.zalando.apidiscovery.storage.domain.service.ApiLifecycleService;
import org.zalando.apidiscovery.storage.repository.ApiRepository;
import org.zalando.apidiscovery.storage.repository.ApplicationRepository;

import javax.persistence.EntityManager;
import java.security.NoSuchAlgorithmException;

// @formatter:off
/**
 * AbstractTestClass that creates a setup for the following layers:
 * <p>
 * +---------+----------+
 * |                    |
 * |      Services      |
 * |                    |
 * +---------+----------+
 *           |
 * +---------+----------+
 * |                    |
 * |     Repositories   |
 * |                    |
 * +-----+--------------+
 *       |
 * +-----+-----+
 * |           |
 * | In Memory |
 * | Database  |
 * |           |
 * +-----------+
 */
// @formatter:on
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = AbstractServiceComponentTest.ServiceComponentTestConfiguration.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
public abstract class AbstractServiceComponentTest {

    @Autowired
    protected ApplicationRepository applicationRepository;

    @Autowired
    protected ApiRepository apiRepository;

    @Autowired
    protected EntityManager entityManager;

    @TestConfiguration
    static class ServiceComponentTestConfiguration {

        @Bean
        public ApiDefinitionProcessingService apiProcessingService(ApplicationRepository applicationRepository,
                                                                   ApiRepository apiRepository,
                                                                   EntityManager entityManager) throws NoSuchAlgorithmException {
            return new ApiDefinitionProcessingService(applicationRepository, apiRepository, entityManager);
        }

        @Bean
        public ApiLifecycleService apiLifecycleService(ApiRepository apiRepository) {
            return new ApiLifecycleService(apiRepository, 1, 1);
        }
    }
}
