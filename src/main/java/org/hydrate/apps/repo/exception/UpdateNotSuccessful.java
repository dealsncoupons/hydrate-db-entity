package org.hydrate.apps.repo.exception;

public class UpdateNotSuccessful extends RuntimeException {
    public UpdateNotSuccessful(String customMessage, Exception e) {
        super(customMessage, e);
    }
}
