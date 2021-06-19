package org.hydrate.apps.repo.exception;

public class RecordNotCreated extends RuntimeException {
    public RecordNotCreated(String customMessage) {
        super(customMessage);
    }
}
