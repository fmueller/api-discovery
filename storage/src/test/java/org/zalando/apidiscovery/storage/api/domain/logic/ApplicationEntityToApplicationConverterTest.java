package org.zalando.apidiscovery.storage.api.domain.logic;

import org.junit.Test;
import org.zalando.apidiscovery.storage.api.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;
import org.zalando.apidiscovery.storage.api.domain.model.Application;
import org.zalando.apidiscovery.storage.api.repository.ApplicationEntity;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.APP1_NAME;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.APP_URL;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.NOW;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.givenApiDeployment;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.givenApiEntity;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.givenApplication;
import static org.zalando.apidiscovery.storage.api.domain.logic.ApplicationEntityToApplicationDtoConverter.toApplication;

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
