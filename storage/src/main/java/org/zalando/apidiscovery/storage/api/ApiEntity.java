package org.zalando.apidiscovery.storage.api;

import lombok.*;

import javax.persistence.*;
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
public class ApiEntity extends AbstractCreateAbleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String apiName;
    private String apiVersion;
    private String definition;
    @OneToMany(mappedBy = "api", cascade = ALL)
    private List<ApiDeploymentEntity> apiDeploymentEntities = new ArrayList<>();


    public void addDeploymentEntity(ApiDeploymentEntity deploymentEntity) {
        apiDeploymentEntities.add(deploymentEntity);
    }
}
