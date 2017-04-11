package org.zalando.apidiscovery.storage.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "api_version")
@ToString(exclude = "apiDeploymentEntities")
public class ApiEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String apiName;
    private String apiVersion;
    private int definitionId;
    private String definitionHash;
    private String definition;
    private String definitionType;

    @OneToMany(mappedBy = "api", cascade = ALL)
    private List<ApiDeploymentEntity> apiDeploymentEntities = new ArrayList<>();

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentOffsetDateTime",
            parameters = {@Parameter(name = "javaZone", value = "UTC")})
    private OffsetDateTime created;

}
