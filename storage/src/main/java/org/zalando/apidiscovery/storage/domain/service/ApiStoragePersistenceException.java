package org.zalando.apidiscovery.storage.domain.service;

public class ApiStoragePersistenceException extends RuntimeException {

    public ApiStoragePersistenceException(String message) {
        super(message);
    }

    public ApiStoragePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
