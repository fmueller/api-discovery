package org.zalando.apidiscovery.storage.domain.service;

public class SwaggerParseException extends RuntimeException {

    public SwaggerParseException(String message) {
        super(message);
    }

    public SwaggerParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
