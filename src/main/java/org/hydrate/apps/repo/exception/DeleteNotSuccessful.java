package org.hydrate.apps.repo.exception;

public class DeleteNotSuccessful extends RuntimeException {
    public DeleteNotSuccessful(String customMessage, Exception e) {
        super(customMessage, e);
    }
}
