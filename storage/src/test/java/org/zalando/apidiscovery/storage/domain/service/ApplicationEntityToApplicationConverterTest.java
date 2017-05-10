package org.zalando.apidiscovery.storage.domain.service;

import org.junit.Test;
import org.zalando.apidiscovery.storage.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.repository.ApiEntity;
import org.zalando.apidiscovery.storage.domain.model.Application;
import org.zalando.apidiscovery.storage.repository.ApplicationEntity;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.DomainObjectGen.APP1_NAME;
import static org.zalando.apidiscovery.storage.DomainObjectGen.APP_URL;
import static org.zalando.apidiscovery.storage.DomainObjectGen.NOW;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApiDeployment;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApiEntity;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApplication;
import static org.zalando.apidiscovery.storage.domain.service.ApplicationEntityToApplicationDtoConverter.toApplication;

public class ApplicationEntityToApplicationConverterTest {

    @Test
    public void shouldConvertEntityWithoutDeploymentsCorrectly() throws Exception {
        ApplicationEntity applicationEntity = givenApplication();

        Application dto = toApplication(applicationEntity);

        assertThat(dto.getAppUrl()).isEqualTo(APP_URL);
        assertThat(dto.getName()).isEqualTo(APP1_NAME);
        assertThat(dto.getCreated()).isEqualTo(NOW);
    }

    @Test
    public void shouldConvertEntityWithDeploymentsCorrectly() throws Exception {
        ApplicationEntity applicationEntity = givenApplication();
        ApiEntity apiEntity1 = givenApiEntity(1, "api", "v1");
        ApiEntity apiEntity2 = givenApiEntity(2, "api", "v2");
        ApiDeploymentEntity deploymentEntity1 = givenApiDeployment(apiEntity1, applicationEntity);
        ApiDeploymentEntity deploymentEntity2 = givenApiDeployment(apiEntity2, applicationEntity);
        applicationEntity.setApiDeploymentEntities(asList(deploymentEntity1, deploymentEntity2));

        Application dto = toApplication(applicationEntity);

        assertThat(dto.getDefinitions()).hasSize(2);
    }

}
