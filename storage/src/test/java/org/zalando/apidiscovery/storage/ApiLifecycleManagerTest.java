package org.zalando.apidiscovery.storage;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneOffset.UTC;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApiLifecycleManagerTest {

    @Test
    public void outdatedApiDefinitionShouldBeMarkedAsInactive() {
        ApiDefinition apiDefinition = spy(new ApiDefinition());
        apiDefinition.setApplicationId("test");
        apiDefinition.setStatus("UNSUCCESSFUL");
        apiDefinition.setLifecycleState(ApiLifecycleManager.ACTIVE);
        List<ApiDefinition> outdatedApis = Collections.singletonList(apiDefinition);

        ApiDefinitionRepository mockedRepository = mock(ApiDefinitionRepository.class);
        when(mockedRepository.findOlderThanAndUnsuccessful(any(OffsetDateTime.class))).thenReturn(outdatedApis);

        ApiLifecycleManager lifecycleManager = new ApiLifecycleManager(mockedRepository, 1, Integer.MAX_VALUE);
        lifecycleManager.inactivateApis(now(UTC));

        verify(mockedRepository).save(outdatedApis);
        verify(apiDefinition).setLifecycleState(ApiLifecycleManager.INACTIVE);
    }

    @Test
    public void outdatedApiDefinitionShouldBeMarkedAsDecommissioned() {
        ApiDefinition apiDefinition = spy(new ApiDefinition());
        apiDefinition.setApplicationId("test");
        apiDefinition.setStatus("UNSUCCESSFUL");
        apiDefinition.setLifecycleState(ApiLifecycleManager.INACTIVE);
        List<ApiDefinition> outdatedApis = Collections.singletonList(apiDefinition);

        ApiDefinitionRepository mockedRepository = mock(ApiDefinitionRepository.class);
        when(mockedRepository.findNotUpdatedSinceAndInactive(any(OffsetDateTime.class))).thenReturn(outdatedApis);

        ApiLifecycleManager lifecycleManager = new ApiLifecycleManager(mockedRepository, 1, Integer.MAX_VALUE);
        lifecycleManager.decomissionApis(now(UTC));

        verify(mockedRepository).save(outdatedApis);
        verify(apiDefinition).setLifecycleState(ApiLifecycleManager.DECOMMISSIONED);
    }
}
