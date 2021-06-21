package org.hydrate.apps.entity;

import org.hydrate.apps.support.Entity;
import org.hydrate.apps.support.EntityInfo;
import org.hydrate.apps.support.EntityMetadata;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TaskStatus implements ITaskStatus, Entity {

    private Boolean completed;
    private LocalDateTime timeStarted;

    public TaskStatus() {
    }

    public TaskStatus(Boolean completed, LocalDateTime timeStarted) {
        this.completed = completed;
        this.timeStarted = timeStarted;
    }

    @Override
    public Boolean completed() {
        return this.completed;
    }

    @Override
    public LocalDateTime timeStarted() {
        return this.timeStarted;
    }

    @Override
    public EntityInfo info() {
        return EntityMetadata.taskStatusInfo();
    }

    @Override
    public Map<String, Object> extractValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("done", this.completed);
        values.put("time_started", this.timeStarted);
        return values;
    }

    @Override
    public <V> void set(String field, V value) {
        switch (field) {
            case "completed":
                this.completed = (Boolean) value;
                break;
            case "timeStarted":
                this.timeStarted = (LocalDateTime) value;
                break;
            default:
                throw new RuntimeException("This field is not yet mapped through the 'set' method");
        }
    }

    @Override
    public <V> V get(String field) {
        switch (field) {
            case "completed":
                return (V) this.completed;
            case "timeStarted":
                return (V) this.timeStarted;
            default:
                throw new RuntimeException("This field is not yet mapped through the 'set' method");
        }
    }

    @Override
    public <ID> ID getKey() {
        throw new RuntimeException("Embedded entities do not have an identity");
    }
}
