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
import java.util.List;
import java.util.Optional;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

@Service
public class ApiDefinitionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDefinitionManager.class);

    private final ApplicationRepository applicationRepository;
    private final ApiRepository apiRepository;
    private final EntityManager entityManager;
    private final SwaggerDefinitionHelper swagger;

    @Autowired
    public ApiDefinitionManager(final ApplicationRepository appRepository,
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
        setApiNameAndVersion(crawledAPIDefinition);
        final OffsetDateTime now = now(UTC);

        final ApiEntity apiVersion = createOrUpdateApiVersion(crawledAPIDefinition, now);
        final ApplicationEntity application = createOrUpdateApplication(crawledAPIDefinition, now);
        final ApiDeploymentEntity apiDeployment = createOrUpdateApiDeployment(apiVersion, application, now);

        LOG.info("New crawling information has been processed; api deployment: {}", apiDeployment);
    }

    private ApiDeploymentEntity createOrUpdateApiDeployment(ApiEntity apiVersion, ApplicationEntity application,
                                                            OffsetDateTime now) {
        final Optional<ApiDeploymentEntity> existingApiDeployment = Optional.ofNullable(
                entityManager.find(ApiDeploymentEntity.class, new ApiDeploymentEntity(apiVersion, application)));
        final ApiDeploymentEntity apiDeployment = existingApiDeployment
                .map(deployment -> deployment)
                .orElse(newApiDeployment(now));

        apiDeployment.setLastCrawled(now);
        apiDeployment.setLifecycleState(ApiLifecycleState.ACTIVE);
        apiDeployment.setApplication(application);
        apiDeployment.setApi(apiVersion);

        entityManager.persist(apiDeployment);
        return apiDeployment;
    }

    private ApplicationEntity createOrUpdateApplication(CrawledApiDefinitionDto crawledAPIDefinition, OffsetDateTime now) {
        final Optional<ApplicationEntity> existingApplication =
                applicationRepository.findOneByName(crawledAPIDefinition.getApplicationName());

        final ApplicationEntity application = existingApplication
                .map(app -> app)
                .orElse(newApplication(crawledAPIDefinition, now));

        return applicationRepository.saveAndFlush(application);
    }

    private ApiEntity createOrUpdateApiVersion(final CrawledApiDefinitionDto crawledAPIDefinition, final OffsetDateTime now) {
        final List<ApiEntity> existingApis = apiRepository.findByApiNameAndApiVersionAndDefinition(
                crawledAPIDefinition.getApiName(),
                crawledAPIDefinition.getVersion(),
                crawledAPIDefinition.getDefinition());

        if (existingApis.isEmpty()) {
            return apiRepository.saveAndFlush(newApiVersion(crawledAPIDefinition, now));
        } else {
            return existingApis.get(0);
        }
    }

    void setApiNameAndVersion(final CrawledApiDefinitionDto crawledAPIDefinition) throws SwaggerParseException {
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
