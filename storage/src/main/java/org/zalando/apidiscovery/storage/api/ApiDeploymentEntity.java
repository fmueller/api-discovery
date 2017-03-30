package org.zalando.apidiscovery.storage.api;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "api_deployment")
public class ApiDeploymentEntity extends AbstractCreateAbleEntity {

    @Id
    @ManyToOne
    private ApiEntity api;

    @Id
    @ManyToOne
    private ApplicationEntity application;

    @Enumerated(EnumType.STRING)
    private ApiLifecycleState lifecycleState;

    //@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentOffsetDateTime")
    private LocalDateTime lastCrawled;
}
