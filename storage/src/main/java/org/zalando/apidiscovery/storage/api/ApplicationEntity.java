package org.zalando.apidiscovery.storage.api;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
public class ApplicationEntity extends AbstractCreateAbleEntity {

    @Id
    private String name;
    private String serviceUrl;
    @OneToMany(mappedBy = "application", cascade = ALL)
    private List<ApiDeploymentEntity> apiDeploymentEntities;

}
