package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.zalando.apidiscovery.crawler.gateway.ApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.LegacyApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.WellKnownSchemaGateway;
import org.zalando.stups.clients.kio.ApplicationBase;

import java.util.concurrent.Callable;

@Slf4j
class ApiDefinitionCrawlJob implements Callable<CrawlResult> {

    private final LegacyApiDiscoveryStorageGateway legacyStorageGateway;
    private final ApiDiscoveryStorageGateway storageGateway;
    private final WellKnownSchemaGateway schemaGateway;
    private final KioApplication app;

    ApiDefinitionCrawlJob(LegacyApiDiscoveryStorageGateway legacyStorageGateway,
                          ApiDiscoveryStorageGateway storageGateway,
                          WellKnownSchemaGateway schemaGateway,
                          ApplicationBase app) {
        this.legacyStorageGateway = legacyStorageGateway;
        this.storageGateway = storageGateway;
        this.schemaGateway = schemaGateway;
        this.app = new KioApplication(app);
    }

    @Override
    public CrawlResult call() throws Exception {
        JsonNode schemaDiscoveryJson = schemaGateway.retrieveSchemaDiscovery(app);

        if (schemaDiscoveryJson == null) {
            log.info("Api definition unavailable for {}", app.getName());
            return pushUnsuccessfulCrawlingResults(app);
        } else {
            final SchemaDiscovery schemaDiscovery = new SchemaDiscovery(schemaDiscoveryJson);
            final CrawledApiDefinition apiDefinition = new CrawledApiDefinition(
                schemaGateway.retrieveApiDefinition(app, schemaDiscovery));
            log.info("Successfully crawled api definition of {}", app.getName());
            return pushCrawlingResults(schemaDiscovery, apiDefinition, app);
        }
    }

    private CrawlResult pushUnsuccessfulCrawlingResults(KioApplication app) {
        legacyStorageGateway.createOrUpdateApiDefinition(null, null, app);
        storageGateway.pushApiDefinition(null, null, app);

        return CrawlResult.builder().successful(false).build();
    }

    private CrawlResult pushCrawlingResults(SchemaDiscovery schemaDiscovery, CrawledApiDefinition crawledApiDefinition, KioApplication app) {
        legacyStorageGateway.createOrUpdateApiDefinition(schemaDiscovery, crawledApiDefinition, app);
        storageGateway.pushApiDefinition(schemaDiscovery, crawledApiDefinition, app);

        return CrawlResult.builder().successful(true).build();
    }

}

