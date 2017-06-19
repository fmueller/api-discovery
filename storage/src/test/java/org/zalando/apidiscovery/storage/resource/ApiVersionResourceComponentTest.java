package org.zalando.apidiscovery.storage.resource;

import org.junit.Test;
import org.zalando.apidiscovery.storage.AbstractComponentTest;
import org.zalando.apidiscovery.storage.repository.ApiDeploymentEntity;
import org.zalando.apidiscovery.storage.repository.ApiEntity;
import org.zalando.apidiscovery.storage.repository.ApplicationEntity;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.zalando.apidiscovery.storage.DomainObjectGen.API_NAME;
import static org.zalando.apidiscovery.storage.DomainObjectGen.API_UI;
import static org.zalando.apidiscovery.storage.DomainObjectGen.API_URL;
import static org.zalando.apidiscovery.storage.DomainObjectGen.API_VERSION_1;
import static org.zalando.apidiscovery.storage.DomainObjectGen.API_VERSION_2;
import static org.zalando.apidiscovery.storage.DomainObjectGen.APP1_NAME;
import static org.zalando.apidiscovery.storage.DomainObjectGen.DEFINITION;
import static org.zalando.apidiscovery.storage.DomainObjectGen.DEFINITION_TYPE;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApiDeployment;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApiEntity;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApplication;
import static org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState.ACTIVE;
import static org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState.DECOMMISSIONED;
import static org.zalando.apidiscovery.storage.domain.model.ApiLifecycleState.INACTIVE;

public class ApiVersionResourceComponentTest extends AbstractComponentTest {

    @Test
    public void shouldGroupVersionOfGivenApi() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_1, 2);
        testAPi200.setDefinition("definition2");

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1, INACTIVE);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));

        final String expectedUrl = localUriBuilder().path("/applications/" + APP1_NAME).toUriString();

        mvc.perform(get("/apis/" + API_NAME + "/versions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.versions", hasSize(1)))
            .andExpect(jsonPath("$.versions..definition", hasItems(DEFINITION, "definition2")))
            .andExpect(jsonPath("$.versions..href", hasItem(expectedUrl)))
            .andExpect(jsonPath("$.versions[0].lifecycle_state", equalTo(ACTIVE.name())))
            .andExpect(jsonPath("$..applications..lifecycle_state", hasItems(ACTIVE.name(), INACTIVE.name())));
    }


    @Test
    public void shouldGroupVersionsOfGivenApi() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_2, 1);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1, INACTIVE);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));

        final String expectedUrl = localUriBuilder().path("/applications/" + APP1_NAME).toUriString();

        mvc.perform(get("/apis/" + API_NAME + "/versions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.versions", hasSize(2)))
            .andExpect(jsonPath("$.versions..api_version", hasItems(API_VERSION_1, API_VERSION_2)))
            .andExpect(jsonPath("$.versions..href", hasItem(expectedUrl)))
            .andExpect(jsonPath("$.versions[0].lifecycle_state", equalTo(ACTIVE.name())))
            .andExpect(jsonPath("$.versions..lifecycle_state", hasItems(ACTIVE.name(), INACTIVE.name())));
    }

    @Test
    public void shouldReturnAllActiveVersions() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_2, 1);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1, INACTIVE);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));

        mvc.perform(get("/apis/" + API_NAME + "/versions?lifecycle_state=ACTIVE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.versions", hasSize(1)))
            .andExpect(jsonPath("$.versions[0].lifecycle_state", equalTo(ACTIVE.name())));
    }

    @Test
    public void shouldReturnAllInactiveVersions() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_2, 1);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1, INACTIVE);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1, DECOMMISSIONED);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));

        mvc.perform(get("/apis/" + API_NAME + "/versions?lifecycle_state=INACTIVE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.versions", hasSize(1)))
            .andExpect(jsonPath("$.versions[0].lifecycle_state", equalTo(INACTIVE.name())));
    }

    @Test
    public void shouldNotFindInactiveVersion() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi100_1 = givenApiEntity(API_NAME, API_VERSION_1, 2);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1, INACTIVE);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi100_1, app1, ACTIVE);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi100_1.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi100_1));

        mvc.perform(get("/apis/" + API_NAME + "/versions?lifecycle_state=INACTIVE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.versions", hasSize(0)));
    }

    @Test
    public void shouldReturnAllDecommissionedVersions() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_2, 2);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1, DECOMMISSIONED);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1, DECOMMISSIONED);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));

        mvc.perform(get("/apis/" + API_NAME + "/versions?lifecycle_state=DECOMMISSIONED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.versions", hasSize(2)))
            .andExpect(jsonPath("$.versions[0].lifecycle_state", equalTo(DECOMMISSIONED.name())));
    }

    @Test
    public void shouldNotFindDecommissionedVersion() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi100_1 = givenApiEntity(API_NAME, API_VERSION_1, 2);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1, DECOMMISSIONED);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi100_1, app1, ACTIVE);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi100_1.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi100_1));

        mvc.perform(get("/apis/" + API_NAME + "/versions?lifecycle_state=DECOMMISSIONED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.versions", hasSize(0)));
    }

    @Test
    public void shouldReturnStatusCode400ForUnknownApiLifecycleState() throws Exception {
        mvc.perform(get("/apis/" + API_NAME + "/versions?lifecycle_state=UNKNOWN"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnOneVersion() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));

        ApiEntity testAPi100 = givenApiEntity(API_NAME, API_VERSION_1, 1);
        ApiEntity testAPi200 = givenApiEntity(API_NAME, API_VERSION_2, 1);

        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1);
        ApiDeploymentEntity testAPi200OnApp1 = givenApiDeployment(testAPi200, app1);

        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));
        testAPi200.setApiDeploymentEntities(asList(testAPi200OnApp1));

        apiRepository.save(asList(testAPi100, testAPi200));
        String exptectedHref = localUriBuilder()
            .path("/applications/" + APP1_NAME)
            .toUriString();

        mvc.perform(get("/apis/" + API_NAME + "/versions/" + API_VERSION_1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.api_version", equalTo(API_VERSION_1)))
            .andExpect(jsonPath("$.lifecycle_state", equalTo(ACTIVE.name())))
            .andExpect(jsonPath("$.definitions[0].type", equalTo(DEFINITION_TYPE)))
            .andExpect(jsonPath("$.definitions[0].definition", equalTo(DEFINITION)))
            .andExpect(jsonPath("$.definitions[0].applications[0].lifecycle_state", equalTo(ACTIVE.name())))
            .andExpect(jsonPath("$.definitions[0].applications[0].api_ui", equalTo(API_UI)))
            .andExpect(jsonPath("$.definitions[0].applications[0].api_url", equalTo(API_URL)))
            .andExpect(jsonPath("$.definitions[0].applications[0].href", equalTo(exptectedHref)));
    }

    @Test
    public void shouldNotTruncateVersionWithDots() throws Exception {
        ApplicationEntity app1 = applicationRepository.save(givenApplication(APP1_NAME));
        ApiEntity testAPi100 = givenApiEntity(API_NAME, "1.0.0", 1);
        ApiDeploymentEntity testAPi100OnApp1 = givenApiDeployment(testAPi100, app1);
        testAPi100.setApiDeploymentEntities(asList(testAPi100OnApp1));

        apiRepository.save(testAPi100);

        mvc.perform(get("/apis/" + API_NAME + "/versions/1.0.0"))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn404IfNoVersionFound() throws Exception {
        mvc.perform(get("/apis/" + API_NAME + "/versions/" + API_VERSION_1))
            .andExpect(status().isNotFound());
    }

}
