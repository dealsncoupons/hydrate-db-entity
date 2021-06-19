package org.hydrate.apps.repo.exception;

public class EntityRelationCycle extends RuntimeException {
    public EntityRelationCycle(String customMessage) {
        super(customMessage);
    }
}
