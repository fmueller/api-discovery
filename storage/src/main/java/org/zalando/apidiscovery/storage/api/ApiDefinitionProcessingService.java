package org.zalando.apidiscovery.storage.api;

import org.hibernate.Session;
import org.hsqldb.HsqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.apidiscovery.storage.utils.SwaggerDefinitionHelper;
import org.zalando.apidiscovery.storage.utils.SwaggerParseException;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
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

    private final MessageDigest messageDigest;


    private final ApplicationRepository applicationRepository;
    private final ApiRepository apiRepository;
    private final EntityManager entityManager;

    @Autowired
    public ApiDefinitionProcessingService(final ApplicationRepository appRepository,
                                          final ApiRepository apiRepository,
                                          final EntityManager entityManager) throws NoSuchAlgorithmException {
        this.applicationRepository = appRepository;
        this.apiRepository = apiRepository;
        this.entityManager = entityManager;
        this.messageDigest = MessageDigest.getInstance("SHA-256");
    }

    @Transactional
    public Optional<ApiEntity> processDiscoveredApiDefinition(final DiscoveredApiDefinition discoveredApiDefinition) {
        setApiNameAndVersion(discoveredApiDefinition);
        final OffsetDateTime now = now(UTC);

        final Session session = entityManager.unwrap(Session.class);
        session.setFlushMode(FlushModeType.COMMIT);

        final ApplicationEntity application = findOrCreateApplication(discoveredApiDefinition, now);
        applicationRepository.save(application);

        for (int retryOnUniqueConstraintViolation = 0; retryOnUniqueConstraintViolation < 100; retryOnUniqueConstraintViolation++) {
            try {
                ApiEntity apiVersion = findOrCreateApiDefinition(session, discoveredApiDefinition, now);
                final ApiDeploymentEntity apiDeployment = findOfCreateApiDeployment(apiVersion, application, now);

                apiVersion = apiRepository.save(apiVersion);
                entityManager.persist(apiDeployment);

                LOG.info("New crawling information has been processed; api deployment: {}", apiDeployment);
                return Optional.of(apiVersion);
            } catch (DataIntegrityViolationException e) {
                Throwable rootCause = e.getRootCause();
                if (!(rootCause instanceof HsqlException
                        && rootCause.getMessage().toUpperCase().contains("API_VERSION_API_NAME_VERSION_DEFINITION_ID_IDX"))) {
                    throw e;
                }
            }
        }
        return Optional.empty();
    }

    private ApiDeploymentEntity findOfCreateApiDeployment(ApiEntity apiVersion, ApplicationEntity application,
                                                          OffsetDateTime now) {
        final boolean apiDeploymentCanExist = entityManager.contains(apiVersion) && entityManager.contains(application);
        final Optional<ApiDeploymentEntity> existingApiDeployment = apiDeploymentCanExist ? Optional.ofNullable(
                entityManager.find(ApiDeploymentEntity.class, new ApiDeploymentEntity(apiVersion, application)))
                : Optional.empty();

        final ApiDeploymentEntity apiDeployment = existingApiDeployment.orElse(newApiDeployment(now));

        apiDeployment.setLastCrawled(now);
        apiDeployment.setLifecycleState(ApiLifecycleState.ACTIVE);
        apiDeployment.setApplication(application);
        apiDeployment.setApi(apiVersion);

        return apiDeployment;
    }

    private ApplicationEntity findOrCreateApplication(DiscoveredApiDefinition discoveredApiDefinition, OffsetDateTime now) {
        final Optional<ApplicationEntity> existingApplication =
                applicationRepository.findOneByName(discoveredApiDefinition.getApplicationName());

        return existingApplication.orElse(newApplication(discoveredApiDefinition, now));
    }

    private ApiEntity findOrCreateApiDefinition(final Session session, final DiscoveredApiDefinition discoveredApiDefinition, final OffsetDateTime now) {
        final ApiEntity api;
        final String definitionHash = sha256(discoveredApiDefinition.getDefinition());
        final List<ApiEntity> existingApis = apiRepository.findByApiNameAndApiVersionAndDefinitionHash(
                discoveredApiDefinition.getApiName(),
                discoveredApiDefinition.getVersion(),
                definitionHash);

        if (existingApis.isEmpty()) {
            final int nextDefinitionId = nextDefinitionId(session, discoveredApiDefinition);
            api = newApiVersion(discoveredApiDefinition, now, definitionHash, nextDefinitionId);
        } else {
            api = existingApis.get(0);
        }
        return api;
    }

    protected int nextDefinitionId(Session session, DiscoveredApiDefinition discoveredApiDefinition) {
        return 1 + apiRepository.getLastApiDefinitionId(
                discoveredApiDefinition.getApiName(),
                discoveredApiDefinition.getVersion());
    }

    private String sha256(String content) {
        messageDigest.update(content.getBytes(StandardCharsets.UTF_8));
        return String.format("%064x", new BigInteger(1, messageDigest.digest()));
    }

    protected void setApiNameAndVersion(final DiscoveredApiDefinition discoveredApiDefinition) throws SwaggerParseException {
        final SwaggerDefinitionHelper swagger = new SwaggerDefinitionHelper(discoveredApiDefinition.getDefinition());
        final String name = swagger.getName();
        final String version = swagger.getVersion();

        discoveredApiDefinition.setApiName(name);
        discoveredApiDefinition.setVersion(version);
    }

    private ApiDeploymentEntity newApiDeployment(OffsetDateTime now) {
        return ApiDeploymentEntity.builder()
                .created(now)
                .build();
    }

    private ApplicationEntity newApplication(DiscoveredApiDefinition discoveredAPIDefinition, OffsetDateTime now) {
        return ApplicationEntity.builder()
                .appUrl(discoveredAPIDefinition.getServiceUrl())
                .name(discoveredAPIDefinition.getApplicationName())
                .apiDeploymentEntities(new ArrayList<>())
                .created(now)
                .build();
    }

    private ApiEntity newApiVersion(DiscoveredApiDefinition discoveredAPIDefinition, OffsetDateTime now,
                                    String definitionHash, int nextDefinitionId) {
        return ApiEntity.builder()
                .apiName(discoveredAPIDefinition.getApiName())
                .apiVersion(discoveredAPIDefinition.getVersion())
                .definition(discoveredAPIDefinition.getDefinition())
                .definitionHash(definitionHash)
                .definitionId(nextDefinitionId)
                .created(now)
                .apiDeploymentEntities(new ArrayList<>())
                .build();
    }
}
