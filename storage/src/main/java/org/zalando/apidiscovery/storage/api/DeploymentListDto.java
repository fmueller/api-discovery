package org.zalando.apidiscovery.storage.api;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentListDto {

    private List<DeploymentDto> deployments = new ArrayList<>();
}
