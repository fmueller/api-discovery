package org.zalando.apidiscovery.storage.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "application")
@ToString(exclude = "apiDeploymentEntities")
public class ApplicationEntity implements Serializable {

    @Id
    private String name;
    private String appUrl;
    @OneToMany(mappedBy = "application", cascade = ALL)
    private List<ApiDeploymentEntity> apiDeploymentEntities;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentOffsetDateTime",
        parameters = {@Parameter(name = "javaZone", value = "UTC")})
    private OffsetDateTime created;

}
