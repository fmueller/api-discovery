package org.zalando.apidiscovery.crawler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.zalando.apidiscovery.crawler.gateway.ApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.LegacyApiDiscoveryStorageGateway;
import org.zalando.apidiscovery.crawler.gateway.WellKnownSchemaGateway;
import org.zalando.stups.clients.kio.ApplicationBase;
import org.zalando.stups.clients.kio.KioOperations;

@Component
@Slf4j
public class ApiDiscoveryCrawler {

    private final KioOperations kioClient;
    private final LegacyApiDiscoveryStorageGateway legacyStorageGateway;
    private final ApiDiscoveryStorageGateway storageGateway;
    private final WellKnownSchemaGateway schemaGateway;
    private final ExecutorService fixedPool;

    @Autowired
    public ApiDiscoveryCrawler(KioOperations kioClient,
                               LegacyApiDiscoveryStorageGateway legacyStorageGateway,
                               ApiDiscoveryStorageGateway storageClient,
                               WellKnownSchemaGateway schemaGateway,
                               @Value("${crawler.jobs.pool}") int jobsPoolSize) {
        this.kioClient = kioClient;
        this.legacyStorageGateway = legacyStorageGateway;
        this.storageGateway = storageClient;
        this.schemaGateway = schemaGateway;
        fixedPool = Executors.newFixedThreadPool(jobsPoolSize);
    }

    @Scheduled(fixedDelayString = "${crawler.delay}")
    public void crawlApiDefinitions() {
        log.info("Start crawling api definitions");

        final List<ApplicationBase> applications = kioClient.listApplications();
        log.info("Found {} applications in kio", applications.size());

        final List<Callable<CrawlResult>> crawlJobs = applications.stream()
                .filter(app -> !StringUtils.isEmpty(app.getServiceUrl()))
                .map(app -> new ApiDefinitionCrawlJob(legacyStorageGateway, storageGateway, schemaGateway, app))
                .collect(Collectors.toList());
        log.info("Crawling {} api definitions", crawlJobs.size());

        try {
            List<Future<CrawlResult>> futures = fixedPool.invokeAll(crawlJobs);
            for (Future<CrawlResult> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while crawling", e);
            // swallow exception to not stop crawler
        }

        log.info("Finished crawling api definitions");
    }
}
