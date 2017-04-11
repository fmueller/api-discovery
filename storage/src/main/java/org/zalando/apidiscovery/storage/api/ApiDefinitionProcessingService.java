package org.zalando.apidiscovery.storage.api;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.apidiscovery.storage.utils.SwaggerDefinitionHelper;
import org.zalando.apidiscovery.storage.utils.SwaggerParseException;

import javax.persistence.EntityManager;
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

    @Autowired
    public ApiDefinitionProcessingService(final ApplicationRepository appRepository,
                                          final ApiRepository apiRepository,
                                          final EntityManager entityManager) {
        this.applicationRepository = appRepository;
        this.apiRepository = apiRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public ApiEntity processDiscoveredApiDefinition(final DiscoveredApiDefinition discoveredApiDefinition) throws NoSuchAlgorithmException {
        setApiNameAndVersion(discoveredApiDefinition);
        final OffsetDateTime now = now(UTC);

        final ApplicationEntity application = findOrCreateApplication(discoveredApiDefinition, now);
        final ApiEntity apiVersion = findOrCreateApiDefinition(discoveredApiDefinition, now);
        final ApiDeploymentEntity apiDeployment = findOfCreateApiDeployment(apiVersion, application, now);

        apiRepository.save(apiVersion);
        applicationRepository.save(application);
        entityManager.persist(apiDeployment);
        LOG.info("New crawling information has been processed; api deployment: {}", apiDeployment);
        return apiVersion;
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

    private ApiEntity findOrCreateApiDefinition(final DiscoveredApiDefinition discoveredApiDefinition, final OffsetDateTime now)
            throws NoSuchAlgorithmException {
        final ApiEntity api;
        final String definitionHash = sha256(discoveredApiDefinition.getDefinition());
        final List<ApiEntity> existingApis = apiRepository.findByApiNameAndApiVersionAndDefinitionHash(
                discoveredApiDefinition.getApiName(),
                discoveredApiDefinition.getVersion(),
                definitionHash);

        if (existingApis.isEmpty()) {
            final Session session = entityManager.unwrap(Session.class);
            final int nextDefinitionId = 1 + (int) session.getNamedQuery("selectLastApiDefinitionId")
                    .setParameter("apiName", discoveredApiDefinition.getApiName())
                    .setParameter("apiVersion", discoveredApiDefinition.getVersion())
                    .getResultList().get(0);

            api = newApiVersion(discoveredApiDefinition, now, definitionHash, nextDefinitionId);
        } else {
            api = existingApis.get(0);
        }
        return api;
    }

    private String sha256(String content) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(content.getBytes(StandardCharsets.UTF_8));
        return String.format("%064x", new BigInteger(1, md.digest()));
    }

    protected void setApiNameAndVersion(final DiscoveredApiDefinition discoveredApiDefinition) throws SwaggerParseException {
        final String name;
        final String version;

        try {
            final SwaggerDefinitionHelper swagger = new SwaggerDefinitionHelper(discoveredApiDefinition.getDefinition());
            name = swagger.getName();
            version = swagger.getVersion();
        } catch (IOException | NullPointerException e) {
            throw new SwaggerParseException("could not parse swagger definition json", e);
        }

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
