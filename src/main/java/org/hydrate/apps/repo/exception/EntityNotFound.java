package org.hydrate.apps.repo.exception;

public class EntityNotFound extends RuntimeException {
    public EntityNotFound(String customMessage) {
        super(customMessage);
    }
}
