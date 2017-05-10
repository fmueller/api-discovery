package org.zalando.apidiscovery.storage.domain.service;

import org.junit.Test;
import org.zalando.apidiscovery.storage.repository.ApiEntity;
import org.zalando.apidiscovery.storage.domain.model.Versions;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.domain.service.ApiEntityToVersionConverter.toVersionList;
import static org.zalando.apidiscovery.storage.DomainObjectGen.givenApiEntity;

public class ApiEntityToVersionConverterTest {

    @Test
    public void shouldConvertVersionDtoStructureCorrectly() throws Exception {
        ApiEntity apiV1_1 = givenApiEntity(1, "api", "v1");
        ApiEntity apiV1_2 = givenApiEntity(2, "api", "v1");
        ApiEntity apiV2 = givenApiEntity(3, "api", "v2");

        List<Versions> versionsList = toVersionList(asList(apiV1_1, apiV1_2, apiV2));

        assertThat(versionsList).hasSize(2);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionBecauseOfFailedPrecondition() {
        ApiEntity apiV1 = givenApiEntity(1, "api", "v1");
        ApiEntity anotherApiV1 = givenApiEntity(2, "anotherApi", "v1");

        toVersionList(asList(apiV1, anotherApiV1));
    }

    @Test
    public void shouldConvertVersionDtoCorrectly() throws Exception {
        ApiEntity apiV1_1 = givenApiEntity(1, "api", "v1");
        ApiEntity apiV1_2 = givenApiEntity(2, "api", "v1");

        List<Versions> versionsList = toVersionList(asList(apiV1_1, apiV1_2));

        assertThat(versionsList).hasSize(1);

        Versions versions = versionsList.get(0);
        assertThat(versions.getApiVersion()).isEqualTo("v1");

    }
}
