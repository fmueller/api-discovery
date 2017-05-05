package org.zalando.apidiscovery.storage.api.domain.logic;

import org.junit.Test;
import org.zalando.apidiscovery.storage.api.domain.model.ApiDefinition;
import org.zalando.apidiscovery.storage.api.DomainObjectGen;
import org.zalando.apidiscovery.storage.api.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;
import org.zalando.apidiscovery.storage.api.repository.ApplicationEntity;
import org.zalando.apidiscovery.storage.api.domain.model.DeploymentLink;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.API_UI;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.API_URL;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.LIFECYCLE_STATE;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.NOW;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.givenApiDeployment;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.givenApiEntity;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.givenApplication;
import static org.zalando.apidiscovery.storage.api.domain.logic.ApiEntityToApiDefinitionConverter.toApiDefinition;

public class ApiEntityToApiDefinitionConverterTest {

    @Test
    public void shouldConvertDefinitionWithoutApplicationsCorrectly() throws Exception {
        ApiEntity apiEntity = givenApiEntity();

        ApiDefinition dto = toApiDefinition(apiEntity);

        assertThat(dto.getDefinition()).isEqualTo(DomainObjectGen.DEFINITION);
        assertThat(dto.getType()).isEqualTo(DomainObjectGen.DEFINITION_TYPE);

    }

    @Test
    public void shouldConvertDefinitionWithApplicationsCorrectly() throws Exception {
        ApplicationEntity applicationEntity = givenApplication();
        ApiEntity apiEntity = givenApiEntity(1, "api", "v1");
        ApiDeploymentEntity deploymentEntity = givenApiDeployment(apiEntity, applicationEntity);
        apiEntity.setApiDeploymentEntities(asList(deploymentEntity));

        ApiDefinition dto = toApiDefinition(apiEntity);

        assertThat(dto.getApplications()).hasSize(1);
        DeploymentLink applicationLink = dto.getApplications().get(0);
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

        ApiDefinition dto = toApiDefinition(apiEntity);

        assertThat(dto.getApplications()).hasSize(2);
    }
}
