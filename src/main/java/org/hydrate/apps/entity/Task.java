package org.hydrate.apps.entity;

import org.hydrate.apps.support.Entity;
import org.hydrate.apps.support.EntityInfo;
import org.hydrate.apps.support.EntityMetadata;

import java.time.LocalDate;
import java.util.*;

public class Task implements ITask, Entity {

    public static final String TABLE_NAME = "tbl_task";

    private UUID id;
    private String name;
    private ITaskStatus status;
    private LocalDate dateCreated;
    private ITask nextTask;
    private ITask dependsOn;
    private Collection<ITask> subTasks = new ArrayList<>();

    public Task() {
    }

    public Task(UUID id, String name, ITaskStatus status, LocalDate dateCreated, Task nextTask, ITask dependsOn, Collection<ITask> subTasks) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.dateCreated = dateCreated;
        this.nextTask = nextTask;
        this.dependsOn = dependsOn;
        this.subTasks = subTasks;
    }

    @Override
    public UUID id() {
        return this.id;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public ITaskStatus status() {
        return this.status;
    }

    @Override
    public LocalDate dateCreated() {
        return this.dateCreated;
    }

    @Override
    public ITask nextTask() {
        return this.nextTask;
    }

    @Override
    public ITask dependsOn() {
        return this.dependsOn;
    }

    @Override
    public Collection<ITask> subTasks() {
        return this.subTasks;
    }

    @Override
    public <V> void set(String field, V value) {
        switch (field) {
            case "id":
                this.id = (UUID) value;
                break;
            case "name":
                this.name = (String) value;
                break;
            case "status":
                this.status = (ITaskStatus) value;
                break;
            case "dateCreated":
                this.dateCreated = (LocalDate) value;
                break;
            case "nextTask":
                this.nextTask = (ITask) value;
                break;
            case "dependsOn":
                this.dependsOn = (ITask) value;
                break;
            case "subTasks":
                this.subTasks = (Collection<ITask>) value;
                break;
            default:
                throw new RuntimeException("This field is not yet mapped through the 'set' method");
        }
    }

    @Override
    public <V> V get(String field) {
        switch (field) {
            case "id":
                return (V) this.id;
            case "name":
                return (V) this.name;
            case "status":
                return (V) this.status;
            case "dateCreated":
                return (V) this.dateCreated;
            case "nextTask":
                return (V) this.nextTask;
            case "dependsOn":
                return (V) this.dependsOn;
            case "subTasks":
                return (V) this.subTasks;
            default:
                throw new RuntimeException("This field is not yet mapped through the 'set' method");
        }
    }

    @Override
    public <ID> ID getKey() {
        return (ID) this.id;
    }

    @Override
    public EntityInfo info() {
        return EntityMetadata.taskInfo();
    }

    @Override
    public Map<String, Object> extractValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("id", this.id);
        values.put("name", this.name);
        values.putAll(((Entity)this.status).extractValues());
        values.put("date_created", this.dateCreated);
        values.put("next_task", this.nextTask != null ? this.nextTask.id() : null);
        values.put("parent_task", this.dependsOn != null ? this.dependsOn.id() : null);
        return values;
    }

    @Override
    public String toString() {
        return "task='" + name + "'";
    }
}