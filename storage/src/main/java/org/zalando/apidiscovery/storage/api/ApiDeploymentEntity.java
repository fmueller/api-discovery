package org.zalando.apidiscovery.storage.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;

import static javax.persistence.CascadeType.ALL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "api_deployment")
public class ApiDeploymentEntity implements Serializable {

    @Id
    @ManyToOne(cascade = ALL)
    private ApiEntity api;

    @Id
    @ManyToOne(cascade = ALL)
    private ApplicationEntity application;

    private String apiUrl;
    private String apiUi;

    @Enumerated(EnumType.STRING)
    private ApiLifecycleState lifecycleState;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentOffsetDateTime",
            parameters = {@Parameter(name = "javaZone", value = "UTC")})
    private OffsetDateTime lastCrawled;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentOffsetDateTime",
            parameters = {@Parameter(name = "javaZone", value = "UTC")})
    private OffsetDateTime created;

    public ApiDeploymentEntity(ApiEntity api, ApplicationEntity application) {
        this.api = api;
        this.application = application;
    }
}
