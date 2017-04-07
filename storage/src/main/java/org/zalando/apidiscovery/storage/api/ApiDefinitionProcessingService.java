package org.zalando.apidiscovery.storage.api;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.apidiscovery.storage.utils.SwaggerDefinitionHelper;
import org.zalando.apidiscovery.storage.utils.SwaggerParseException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;

@Service
public class ApiDefinitionProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDefinitionProcessingService.class);

    private final ApplicationRepository applicationRepository;
    private final ApiRepository apiRepository;
    private final EntityManager entityManager;
    private final SwaggerDefinitionHelper swagger;

    @Autowired
    public ApiDefinitionProcessingService(final ApplicationRepository appRepository,
                                          final ApiRepository apiRepository,
                                          final EntityManager entityManager,
                                          final SwaggerDefinitionHelper swaggerHelper) {
        this.applicationRepository = appRepository;
        this.apiRepository = apiRepository;
        this.entityManager = entityManager;
        this.swagger = swaggerHelper;
    }

    @Transactional
    public ApiEntity processCrawledApiDefinition(final CrawledApiDefinitionDto crawledAPIDefinition) throws NoSuchAlgorithmException {
        setApiNameAndVersion(crawledAPIDefinition);
        final OffsetDateTime now = now(UTC);

        final ApiEntity apiVersion = createOrUpdateApiVersion(crawledAPIDefinition, now);
        final ApplicationEntity application = createOrUpdateApplication(crawledAPIDefinition, now);
        final ApiDeploymentEntity apiDeployment = createOrUpdateApiDeployment(apiVersion, application, now);

        LOG.info("New crawling information has been processed; api deployment: {}", apiDeployment);
        return apiVersion;
    }

    private ApiDeploymentEntity createOrUpdateApiDeployment(ApiEntity apiVersion, ApplicationEntity application,
                                                            OffsetDateTime now) {
        final Optional<ApiDeploymentEntity> existingApiDeployment = Optional.ofNullable(
                entityManager.find(ApiDeploymentEntity.class, new ApiDeploymentEntity(apiVersion, application)));
        final ApiDeploymentEntity apiDeployment = existingApiDeployment.orElse(newApiDeployment(now));

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

        return existingApplication.orElse(applicationRepository.save(newApplication(crawledAPIDefinition, now)));
    }

    private ApiEntity createOrUpdateApiVersion(final CrawledApiDefinitionDto crawledAPIDefinition, final OffsetDateTime now)
            throws NoSuchAlgorithmException {
        final String definitionHash = sha256(crawledAPIDefinition.getDefinition());
        final List<ApiEntity> existingApis = apiRepository.findByApiNameAndApiVersionAndDefinitionHash(
                crawledAPIDefinition.getApiName(),
                crawledAPIDefinition.getVersion(),
                definitionHash);

        if (existingApis.isEmpty()) {
            final Session session = entityManager.unwrap(Session.class);
            final int nextDefinitionId = 1 + (int) session.getNamedQuery("selectLastApiDefinitionId")
                    .setParameter("apiName", crawledAPIDefinition.getApiName())
                    .setParameter("apiVersion", crawledAPIDefinition.getVersion())
                    .getResultList().get(0);
            final ApiEntity newApi = newApiVersion(crawledAPIDefinition, now, definitionHash, nextDefinitionId);

            return apiRepository.save(newApi);
        } else {
            return existingApis.get(0);
        }
    }

    private String sha256(String content) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(content.getBytes(StandardCharsets.UTF_8));
        return String.format("%064x", new BigInteger(1, md.digest()));
    }

    protected void setApiNameAndVersion(final CrawledApiDefinitionDto crawledAPIDefinition) throws SwaggerParseException {
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

    private ApiEntity newApiVersion(CrawledApiDefinitionDto crawledAPIDefinitionDto, OffsetDateTime now,
                                    String definitionHash, int nextDefinitionId) {
        return ApiEntity.builder()
                .apiName(crawledAPIDefinitionDto.getApiName())
                .apiVersion(crawledAPIDefinitionDto.getVersion())
                .definition(crawledAPIDefinitionDto.getDefinition())
                .definitionHash(definitionHash)
                .definitionId(nextDefinitionId)
                .created(now)
                .apiDeploymentEntities(new ArrayList<>())
                .build();
    }
}
