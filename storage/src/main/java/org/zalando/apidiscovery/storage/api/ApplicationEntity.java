package org.zalando.apidiscovery.storage.api;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application")
@ToString(exclude = "apiEntities")
public class ApplicationEntity implements Serializable {

    @Id
    private String name;
    private String serviceUrl;
    private String crawledState;
    private LocalDateTime lastCrawled;
    private LocalDateTime created;

    @OneToMany(mappedBy = "application", cascade = ALL)
    private List<ApiEntity> apiEntities;

}
