package org.zalando.apidiscovery.storage.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.apidiscovery.storage.utils.ApiStoragePersistenceException;
import org.zalando.apidiscovery.storage.utils.SwaggerParseException;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import javax.ws.rs.core.Response;

@ControllerAdvice
class ApiStorageExceptionHandler implements ProblemHandling {

    @ExceptionHandler(SwaggerParseException.class)
    public ResponseEntity<Problem> handleSwaggerParseException(Throwable throwable, NativeWebRequest request) {
        return create(Response.Status.BAD_REQUEST, throwable, request);
    }

    @ExceptionHandler(ApiStoragePersistenceException.class)
    public ResponseEntity<Problem> handleApiStoragePersistenceException(Throwable throwable, NativeWebRequest request) {
        return create(Response.Status.INTERNAL_SERVER_ERROR, throwable, request);
    }
}
