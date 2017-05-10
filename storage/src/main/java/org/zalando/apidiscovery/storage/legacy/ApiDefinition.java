package org.zalando.apidiscovery.storage.legacy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "api")
class ApiDefinition {

    @Id
    private String applicationId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String lifecycleState;

    private String type;

    private String name;

    private String version;

    private String serviceUrl;

    private String url;

    private String ui;

    private String definition;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentOffsetDateTime")
    private OffsetDateTime created;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentOffsetDateTime")
    private OffsetDateTime lastChanged;

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentOffsetDateTime")
    private OffsetDateTime lastPersisted;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLifecycleState() {
        return lifecycleState;
    }

    public void setLifecycleState(String lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public OffsetDateTime getLastChanged() {
        return lastChanged;
    }

    public OffsetDateTime getLastPersisted() {
        return lastPersisted;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public void setLastChanged(OffsetDateTime lastChanged) {
        this.lastChanged = lastChanged;
    }

    public void setLastPersisted(OffsetDateTime lastPersisted) {
        this.lastPersisted = lastPersisted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiDefinition that = (ApiDefinition) o;

        return Objects.equals(applicationId, that.applicationId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(type, that.type) &&
                Objects.equals(name, that.name) &&
                Objects.equals(version, that.version) &&
                Objects.equals(serviceUrl, that.serviceUrl) &&
                Objects.equals(url, that.url) &&
                Objects.equals(ui, that.ui) &&
                Objects.equals(definition, that.definition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationId, status, type, name, version, serviceUrl, url, ui, definition);
    }
}
