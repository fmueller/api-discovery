package org.zalando.apidiscovery.storage;

import org.junit.Test;
import org.zalando.apidiscovery.storage.api.ApiDefinitionDto;
import org.zalando.apidiscovery.storage.api.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.ApiEntity;
import org.zalando.apidiscovery.storage.api.ApiEntityToApiDefinitionConverter;
import org.zalando.apidiscovery.storage.api.ApplicationEntity;
import org.zalando.apidiscovery.storage.api.DeploymentLinkDto;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.DomainObjectGen.API_UI;
import static org.zalando.apidiscovery.storage.DomainObjectGen.API_URL;
import static org.zalando.apidiscovery.storage.DomainObjectGen.DEFINITION_ID;
import static org.zalando.apidiscovery.storage.DomainObjectGen.LIFECYCLE_STATE;
import static org.zalando.apidiscovery.storage.DomainObjectGen.NOW;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApiDeployment;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApiEntity;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApplication;
import static org.zalando.apidiscovery.storage.api.ApiEntityToApiDefinitionConverter.toApiDefinitionDto;

public class ApiEntityToApiDefinitionConverterTest {

    @Test
    public void shouldConvertDefinitionWithoutApplicationsCorrectly() throws Exception {
        ApiEntity apiEntity = givenApiEntity();

        ApiDefinitionDto dto = toApiDefinitionDto(apiEntity);

        assertThat(dto.getId()).isEqualTo(valueOf(DEFINITION_ID));
        assertThat(dto.getDefinition()).isEqualTo(DomainObjectGen.DEFINITION);
        assertThat(dto.getType()).isEqualTo(DomainObjectGen.DEFINITION_TYPE);

    }


    @Test
    public void shouldConvertDefinitionWithApplicationsCorrectly() throws Exception {
        ApplicationEntity applicationEntity = givenApplication();
        ApiEntity apiEntity = givenApiEntity(1, "api", "v1");
        ApiDeploymentEntity deploymentEntity = givenApiDeployment(apiEntity, applicationEntity);
        apiEntity.setApiDeploymentEntities(asList(deploymentEntity));

        ApiDefinitionDto dto = toApiDefinitionDto(apiEntity);

        assertThat(dto.getApplications()).hasSize(1);
        DeploymentLinkDto applicationLink = dto.getApplications().get(0);
        assertThat(applicationLink.getApiUi()).isEqualTo(API_UI);
        assertThat(applicationLink.getApiUrl()).isEqualTo(API_URL);
        assertThat(applicationLink.getCreated()).isEqualTo(NOW);
        assertThat(applicationLink.getLastUpdated()).isEqualTo(NOW);
        assertThat(applicationLink.getLifecycleState()).isEqualTo(LIFECYCLE_STATE);
    }

    @Test
    public void shouldConvertDefinitionWithApplicationsStructureCorrectly() throws Exception {
        ApplicationEntity applicationEntity1 = givenApplication();
        ApplicationEntity applicationEntity2 = givenApplication();
        ApiEntity apiEntity = givenApiEntity(1, "api", "v1");
        ApiDeploymentEntity deploymentEntity1 = givenApiDeployment(apiEntity, applicationEntity1);
        ApiDeploymentEntity deploymentEntity2 = givenApiDeployment(apiEntity, applicationEntity2);
        apiEntity.setApiDeploymentEntities(asList(deploymentEntity1, deploymentEntity2));

        ApiDefinitionDto dto = toApiDefinitionDto(apiEntity);

        assertThat(dto.getApplications()).hasSize(2);

    }
}
