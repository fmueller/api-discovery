package org.zalando.apidiscovery.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.dropwizard.DropwizardMetricServices;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MetricsCollector {

    private final DropwizardMetricServices metricServices;
    private final ApiDefinitionRepository repository;

    @Autowired
    public MetricsCollector(DropwizardMetricServices metricServices, ApiDefinitionRepository repository) {
        this.metricServices = metricServices;
        this.repository = repository;
    }

    @Scheduled(fixedDelayString = "${metrics-collecting.delay}")
    public void collectMetrics() {
        repository.countStatus().forEach(c ->
                metricServices.submit("gauge.apis.crawled." + c.getStatus().toLowerCase(), c.getCount()));
        repository.countLifecycleStates().forEach(c ->
                metricServices.submit("gauge.apis." + c.getStatus().toLowerCase(), c.getCount()));
    }
}
