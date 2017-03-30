package org.zalando.apidiscovery.storage.api;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class AbstractCreateAbleEntity implements Serializable {

    //@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentOffsetDateTime")
    private LocalDateTime created;
}
