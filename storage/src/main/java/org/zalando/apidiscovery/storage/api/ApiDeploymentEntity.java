package org.zalando.apidiscovery.storage.api;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
