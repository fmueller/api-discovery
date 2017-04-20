package org.zalando.apidiscovery.storage;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.apidiscovery.storage.api.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.ApiEntity;
import org.zalando.apidiscovery.storage.api.ApplicationEntity;
import org.zalando.apidiscovery.storage.api.DeploymentLinkDto;
import org.zalando.apidiscovery.storage.api.LinkBuilderUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

public class LinkBuilderUtilTest {

    UriComponentsBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = newInstance()
            .scheme("http")
            .port("8080")
            .host("localhost");
    }

    @Test
    public void shouldCreateApplicationLink() throws Exception {
        assertThat(LinkBuilderUtil.buildApplicationDeploymentLink(builder, "order-management-application").toString())
            .isEqualTo("http://localhost:8080/applications/order-management-application");
    }

    @Test
    public void shouldCreateDefinitionLink() throws Exception {
        ApiEntity apiEntity = ApiEntity.builder()
            .apiName("order-management-api")
            .apiVersion("1.0.0")
            .definitionId(1)
            .build();
        assertThat(LinkBuilderUtil.buildDefinitionDeploymentLink(builder, apiEntity).toString())
            .isEqualTo("http://localhost:8080/apis/order-management-api/versions/1.0.0/definitions/1");

    }

    @Test
    public void shouldCreateDefinitionLinkForApplicationLinkDtoInput() throws Exception {
        ApplicationEntity applicationEntity = ApplicationEntity.builder()
            .name("order-management-application")
            .build();

        ApiDeploymentEntity deploymentEntity = ApiDeploymentEntity.builder()
            .application(applicationEntity)
            .build();

        DeploymentLinkDto.ApplicationLinkDto applicationLinkDto = new DeploymentLinkDto.ApplicationLinkDto(deploymentEntity);

        assertThat(LinkBuilderUtil.buildLink(builder, applicationLinkDto).toString())
            .isEqualTo("http://localhost:8080/applications/order-management-application");
    }

    @Test
    public void shouldCreateDefinitionLinkForDefinitionLinkDtoInput() throws Exception {
        ApiEntity apiEntity = ApiEntity.builder()
            .apiName("order-management-api")
            .apiVersion("1.0.0")
            .definitionId(1)
            .build();

        ApiDeploymentEntity deploymentEntity = ApiDeploymentEntity.builder()
            .api(apiEntity)
            .build();

        DeploymentLinkDto.DefinitionLinkDto definitionLinkDto = new DeploymentLinkDto.DefinitionLinkDto(deploymentEntity);

        assertThat(LinkBuilderUtil.buildLink(builder, definitionLinkDto).toString())
            .isEqualTo("http://localhost:8080/apis/order-management-api/versions/1.0.0/definitions/1");
    }

}
