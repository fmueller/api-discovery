package org.zalando.apidiscovery.storage.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "api_version")
public class ApiEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String apiName;
    private String apiVersion;
    private String definition;
    @Enumerated(EnumType.STRING)
    private ApiLifecycleState lifecycleState;
    private String url;
    private String ui;
    private LocalDateTime lastContentChange;
    private LocalDateTime created;
    @ManyToOne
    private ApplicationEntity application;
}
