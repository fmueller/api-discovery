package org.zalando.apidiscovery.storage.api;

import org.hibernate.Session;
import org.hsqldb.HsqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.apidiscovery.storage.utils.ApiStoragePersistenceException;
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

    @Value("${storage.retries.unique-key-constraint-violation}")
    private int maxNumberOfRetries = 100;

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
    public ApiEntity processDiscoveredApiDefinition(final DiscoveredApiDefinition discoveredApiDefinition) {
        setApiNameAndVersion(discoveredApiDefinition);
        final OffsetDateTime now = now(UTC);
        final String definitionHash = sha256(discoveredApiDefinition.getDefinition());

        // needed in order to implement the retry logic
        // default FlushModeType.AUTO tries to flush before the transaction ends
        final Session session = entityManager.unwrap(Session.class);
        session.setFlushMode(FlushModeType.COMMIT);

        final ApplicationEntity application = findOrCreateApplication(discoveredApiDefinition, now);
        applicationRepository.save(application);

        Optional<ApiEntity> apiVersionOption = findApiDefinition(discoveredApiDefinition.getApiName(),
                discoveredApiDefinition.getVersion(), definitionHash);

        for (int counter = 0; counter < maxNumberOfRetries; counter++) {
            try {
                ApiEntity apiVersion = apiVersionOption.orElse(
                        newApiVersion(discoveredApiDefinition, now, definitionHash, nextDefinitionId(discoveredApiDefinition)));
                final ApiDeploymentEntity apiDeployment = findOrCreateApiDeployment(apiVersion, application, now);

                apiVersion = apiRepository.save(apiVersion);
                entityManager.persist(apiDeployment);

                LOG.info("New crawling information has been processed; api deployment: {}", apiDeployment);
                return apiVersion;
            } catch (DataIntegrityViolationException e) {
                Throwable rootCause = e.getRootCause();
                if (!(rootCause instanceof HsqlException
                        && rootCause.getMessage().toUpperCase().contains("API_VERSION_API_NAME_VERSION_DEFINITION_ID_IDX"))) {
                    LOG.warn("could not persist discovered api definition {}", e);
                    throw e;
                }
            }
        }
        LOG.warn("could not persist discovered api definition: {}", discoveredApiDefinition);
        throw new ApiStoragePersistenceException("could not persist discovered api definition");
    }

    private ApiDeploymentEntity findOrCreateApiDeployment(ApiEntity apiVersion, ApplicationEntity application,
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

    private Optional<ApiEntity> findApiDefinition(String apiName, String apiVersion, String definitionHash) {
        final List<ApiEntity> existingApis = apiRepository.findByApiNameAndApiVersionAndDefinitionHash(
                apiName, apiVersion, definitionHash);

        return existingApis.isEmpty() ? Optional.empty() : Optional.of(existingApis.get(0));
    }

    protected int nextDefinitionId(DiscoveredApiDefinition discoveredApiDefinition) {
        return 1 + apiRepository.getLastApiDefinitionId(
                discoveredApiDefinition.getApiName(),
                discoveredApiDefinition.getVersion());
    }

    private String sha256(String content) {
        messageDigest.reset();
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
                .definitionType(discoveredAPIDefinition.getType())
                .apiVersion(discoveredAPIDefinition.getVersion())
                .definition(discoveredAPIDefinition.getDefinition())
                .definitionHash(definitionHash)
                .definitionId(nextDefinitionId)
                .created(now)
                .apiDeploymentEntities(new ArrayList<>())
                .build();
    }
}
