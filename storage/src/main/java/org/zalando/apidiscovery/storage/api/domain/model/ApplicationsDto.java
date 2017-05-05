package org.zalando.apidiscovery.storage.api.domain.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationsDto {

    private List<ApplicationDto> applications = new ArrayList<>();
}
