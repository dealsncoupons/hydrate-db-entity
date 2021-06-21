package org.hydrate.apps.entity;

import org.hydrate.apps.support.Entity;
import org.hydrate.apps.support.EntityInfo;
import org.hydrate.apps.support.EntityMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssignmentId implements IAssignmentId, Entity {

    private UUID taskId;
    private UUID playerId;

    @Override
    public UUID taskId() {
        return this.taskId;
    }

    @Override
    public UUID playerId() {
        return this.playerId;
    }

    @Override
    public EntityInfo info() {
        return EntityMetadata.assignmentIdInfo();
    }

    @Override
    public Map<String, Object> extractValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("task_id", this.taskId);
        values.put("player_id", this.playerId);
        return values;
    }

    @Override
    public <V> void set(String field, V value) {
        switch (field) {
            case "taskId":
                this.taskId = (UUID) value;
                break;
            case "playerId":
                this.playerId = (UUID) value;
                break;
            default:
                throw new RuntimeException("This field is not yet mapped through the 'set' method");
        }
    }

    @Override
    public <V> V get(String field) {
        switch (field) {
            case "taskId":
                return (V) this.taskId;
            case "playerId":
                return (V) this.playerId;
            default:
                throw new RuntimeException("This field is not yet mapped through the 'set' method");
        }
    }

    @Override
    public <ID> ID getKey() {
        return null;
    }
}
