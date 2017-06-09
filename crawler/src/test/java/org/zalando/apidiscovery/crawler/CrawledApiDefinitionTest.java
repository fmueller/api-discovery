package org.zalando.apidiscovery.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.crawler.TestDataHelper.parseResource;

@RunWith(SpringJUnit4ClassRunner.class)
public class CrawledApiDefinitionTest {

    @Value("classpath:meta_api_definition.json")
    private Resource metaApiDefinitionJson;

    private CrawledApiDefinition crawledApiDefinition;

    @Before
    public void setUp() throws Exception {
        crawledApiDefinition = new CrawledApiDefinition(parseResource(metaApiDefinitionJson));
    }

    @Test
    public void shouldExtractName() throws Exception {
        assertThat(crawledApiDefinition.getName()).isEqualTo("meta-api");
    }

    @Test
    public void shouldExtractEmptyNameForNonExistingField() throws Exception {
        assertThat(new CrawledApiDefinition(new ObjectMapper().createObjectNode()).getName()).isEmpty();
    }

    @Test
    public void shouldExtractVersion() throws Exception {
        assertThat(crawledApiDefinition.getVersion()).isEqualTo("1.0");
    }
}
