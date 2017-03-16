package org.zalando.apidiscovery.storage.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application")
public class ApplicationEntity {

    @Id
    private String name;
    private String serviceUrl;
    private String crawledState;
    private LocalDateTime lastCrawled;
    private LocalDateTime created;

    @OneToMany(mappedBy = "application", cascade = ALL)
    private List<ApiEntity> apiEntities;

}
