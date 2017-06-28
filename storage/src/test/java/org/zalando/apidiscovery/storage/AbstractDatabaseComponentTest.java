package org.zalando.apidiscovery.storage;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.apidiscovery.storage.repository.ApiRepository;
import org.zalando.apidiscovery.storage.repository.ApplicationRepository;

// @formatter:off
/**
 * AbstractTestClass that creates a setup for the following layers:
 * <p>
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
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
public abstract class AbstractDatabaseComponentTest {

    @Autowired
    protected ApplicationRepository applicationRepository;

    @Autowired
    protected ApiRepository apiRepository;

}
