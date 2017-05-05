package org.zalando.apidiscovery.storage.api.domain.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Deployments {

    private List<Deployment> deployments = new ArrayList<>();
}
