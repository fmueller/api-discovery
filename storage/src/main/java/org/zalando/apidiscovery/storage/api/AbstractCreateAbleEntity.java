package org.zalando.apidiscovery.storage.api;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public abstract class AbstractCreateAbleEntity implements Serializable {

    //@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentOffsetDateTime")
    private LocalDateTime created;
}
