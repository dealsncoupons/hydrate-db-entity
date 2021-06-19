package org.hydrate.apps.repo.exception;

public class SaveNotSuccessful extends RuntimeException {
    public SaveNotSuccessful(String customMessage, Exception e) {
        super(customMessage, e);
    }
}
