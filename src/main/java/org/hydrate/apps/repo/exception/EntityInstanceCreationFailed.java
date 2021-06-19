package org.hydrate.apps.repo.exception;

public class EntityInstanceCreationFailed extends RuntimeException {
    public EntityInstanceCreationFailed(String customMessage, Exception e) {
        super(customMessage, e);
    }
}
