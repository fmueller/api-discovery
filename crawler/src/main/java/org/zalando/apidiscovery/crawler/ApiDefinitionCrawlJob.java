package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.apidiscovery.crawler.gateway.ApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.LegacyApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.WellKnownSchemaGateway;
import org.zalando.stups.clients.kio.ApplicationBase;

import java.util.concurrent.Callable;

class ApiDefinitionCrawlJob implements Callable<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDefinitionCrawlJob.class);

    private final LegacyApiDiscoveryStorageGateway legacyStorageGateway;
    private final ApiDiscoveryStorageGateway storageGateway;
    private final WellKnownSchemaGateway schemaGateway;
    private final ApplicationBase app;

    ApiDefinitionCrawlJob(LegacyApiDiscoveryStorageGateway legacyStorageGateway,
                          ApiDiscoveryStorageGateway storageGateway,
                          WellKnownSchemaGateway schemaGateway,
                          ApplicationBase app) {
        this.legacyStorageGateway = legacyStorageGateway;
        this.storageGateway = storageGateway;
        this.schemaGateway = schemaGateway;
        this.app = app;
    }

    @Override
    public Void call() throws Exception {
        final JsonNode schemaDiscovery = schemaGateway.retrieveSchemaDiscovery(app);

        if (schemaDiscovery == null) {
            LOG.info("Api definition unavailable for {}", app.getId());
            pushApiDefinitionToLegacyAndNewEndpoint(null, null, app);
        } else {
            JsonNode apiDefinition = schemaGateway.retrieveApiDefinition(app, schemaDiscovery);
            LOG.info("Successfully crawled api definition of {}", app.getId());
            pushApiDefinitionToLegacyAndNewEndpoint(schemaDiscovery, apiDefinition, app);
        }
        return null;
    }

    private void pushApiDefinitionToLegacyAndNewEndpoint(JsonNode schemaDiscovery, JsonNode apiDefinition, ApplicationBase app) {
        legacyStorageGateway.createOrUpdateApiDefinition(schemaDiscovery, apiDefinition, app);
        storageGateway.pushApiDefinition(schemaDiscovery, apiDefinition, app);
    }

}

