package org.zalando.apidiscovery.storage.api.domain.util;

import org.junit.Test;
import org.zalando.apidiscovery.storage.api.repository.ApiEntity;
import org.zalando.apidiscovery.storage.api.service.dto.VersionsDto;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.zalando.apidiscovery.storage.api.domain.util.ApiEntityToVersionConverter.toVersionDtoList;
import static org.zalando.apidiscovery.storage.api.DomainObjectGen.givenApiEntity;

public class ApiEntityToVersionConverterTest {

    @Test
    public void shouldConvertVersionDtoStructureCorrectly() throws Exception {
        ApiEntity apiV1_1 = givenApiEntity(1, "api", "v1");
        ApiEntity apiV1_2 = givenApiEntity(2, "api", "v1");
        ApiEntity apiV2 = givenApiEntity(3, "api", "v2");

        List<VersionsDto> versionsDtoList = toVersionDtoList(asList(apiV1_1, apiV1_2, apiV2));

        assertThat(versionsDtoList).hasSize(2);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionBecauseOfFailedPrecondition() {
        ApiEntity apiV1 = givenApiEntity(1, "api", "v1");
        ApiEntity anotherApiV1 = givenApiEntity(2, "anotherApi", "v1");

        toVersionDtoList(asList(apiV1, anotherApiV1));
    }

    @Test
    public void shouldConvertVersionDtoCorrectly() throws Exception {
        ApiEntity apiV1_1 = givenApiEntity(1, "api", "v1");
        ApiEntity apiV1_2 = givenApiEntity(2, "api", "v1");

        List<VersionsDto> versionsDtoList = toVersionDtoList(asList(apiV1_1, apiV1_2));

        assertThat(versionsDtoList).hasSize(1);

        VersionsDto versionsDto = versionsDtoList.get(0);
        assertThat(versionsDto.getApiVersion()).isEqualTo("v1");

    }
}
