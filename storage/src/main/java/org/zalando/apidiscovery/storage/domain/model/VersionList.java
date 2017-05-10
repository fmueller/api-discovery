package org.zalando.apidiscovery.storage.domain.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionList {

    private List<Versions> versions = new ArrayList<>();

}
