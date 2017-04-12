package org.zalando.apidiscovery.storage.api;

import static java.text.MessageFormat.format;

public class ApiLifecycleStateNotSupportedException extends RuntimeException {

    public ApiLifecycleStateNotSupportedException(ApiLifecycleState lifecycleState) {
        super(format("ApiLifecycleState [{0}] is not supported!", lifecycleState));
    }
}
