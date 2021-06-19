package org.hydrate.apps.repo.exception;

public class SelectEntityFailure extends RuntimeException {
    public SelectEntityFailure(String customMessage, Exception e) {
        super(customMessage, e);
    }
}
