package org.zalando.apidiscovery.storage.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.apidiscovery.storage.utils.SwaggerDefinitionHelper;
import org.zalando.apidiscovery.storage.utils.SwaggerParseException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

@Service
public class CrawlerService {

    private static final Logger LOG = LoggerFactory.getLogger(CrawlerService.class);

    private ApplicationRepository applicationRepository;
    private ApiRepository apiRepository;
    private EntityManager entityManager;
    private SwaggerDefinitionHelper swagger;

    @Autowired
    public CrawlerService(final ApplicationRepository appRepository,
                          final ApiRepository apiRepository,
                          final EntityManager entityManager,
                          final SwaggerDefinitionHelper swaggerHelper) {
        this.applicationRepository = appRepository;
        this.apiRepository = apiRepository;
        this.entityManager = entityManager;
        this.swagger = swaggerHelper;
    }

    @Transactional
    public void processCrawledApiDefinition(final CrawledApiDefinitionDto crawledAPIDefinition) {
        final OffsetDateTime now = now(UTC);

        setApiNameAndVersion(crawledAPIDefinition);

        ApiEntity apiVersion = apiRepository.saveAndFlush(newApiVersion(crawledAPIDefinition, now));

        final Optional<ApplicationEntity> applicationOption =
                applicationRepository.findOneByName(crawledAPIDefinition.getApplicationName());
        ApplicationEntity application = applicationOption.isPresent() ?
                applicationOption.get() : newApplication(crawledAPIDefinition, now);
        application = applicationRepository.saveAndFlush(application);

        final ApiDeploymentEntity apiDeployment = newApiDeployment(now);
        apiDeployment.setApplication(application);
        apiDeployment.setApi(apiVersion);

        entityManager.persist(apiDeployment);

        LOG.info("Received and processed a new crawling information; api-deployment-entity: {}", apiDeployment);
    }

    void setApiNameAndVersion(CrawledApiDefinitionDto crawledAPIDefinition) throws SwaggerParseException {
        final String name;
        final String version;

        try {
            name = swagger.nameOf(crawledAPIDefinition.getDefinition());
            version = swagger.versionOf(crawledAPIDefinition.getDefinition());
        } catch (IOException | NullPointerException e) {
            throw new SwaggerParseException("could not parse swagger definition json", e);
        }

        crawledAPIDefinition.setApiName(name);
        crawledAPIDefinition.setVersion(version);
    }

    private ApiDeploymentEntity newApiDeployment(OffsetDateTime now) {
        return ApiDeploymentEntity.builder()
                .created(now)
                .lastCrawled(now)
                .lifecycleState(ApiLifecycleState.ACTIVE)
                .build();
    }

    private ApplicationEntity newApplication(CrawledApiDefinitionDto crawledAPIDefinitionDto, OffsetDateTime now) {
        return ApplicationEntity.builder()
                .appUrl(crawledAPIDefinitionDto.getServiceUrl())
                .name(crawledAPIDefinitionDto.getApplicationName())
                .apiDeploymentEntities(new ArrayList<>())
                .created(now)
                .build();
    }

    private ApiEntity newApiVersion(CrawledApiDefinitionDto crawledAPIDefinitionDto, OffsetDateTime now) {
        return ApiEntity.builder()
                .apiName(crawledAPIDefinitionDto.getApiName())
                .apiVersion(crawledAPIDefinitionDto.getVersion())
                .definition(crawledAPIDefinitionDto.getDefinition())
                .created(now)
                .apiDeploymentEntities(new ArrayList<>())
                .build();
    }
}
