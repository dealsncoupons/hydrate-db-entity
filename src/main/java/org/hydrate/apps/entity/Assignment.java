package org.hydrate.apps.entity;

import org.hydrate.apps.support.Entity;
import org.hydrate.apps.support.EntityInfo;
import org.hydrate.apps.support.EntityMetadata;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Assignment implements IAssignment, Entity {

    public static final String TABLE_NAME = "tbl_assignment";

    private AssignmentId id;
    private LocalDate dateAssigned;

    public Assignment() {
    }

    public Assignment(AssignmentId id, LocalDate dateAssigned) {
        this.id = id;
        this.dateAssigned = dateAssigned;
    }

    @Override
    public AssignmentId id() {
        return this.id;
    }

    @Override
    public LocalDate dateAssigned() {
        return this.dateAssigned;
    }

    @Override
    public EntityInfo info() {
        return EntityMetadata.assignmentInfo();
    }

    @Override
    public Map<String, Object> extractValues() {
        Map<String, Object> values = new HashMap<>();
        values.putAll(((Entity)this.id).extractValues());
        values.put("date_assigned", this.dateAssigned);
        return values;
    }

    @Override
    public <V> void set(String field, V value) {
        switch (field) {
            case "id":
                this.id = (AssignmentId) value;
                break;
            case "dateAssigned":
                this.dateAssigned = (LocalDate) value;
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
            case "dateAssigned":
                return (V) this.dateAssigned;
            default:
                throw new RuntimeException("This field is not yet mapped through the 'set' method");
        }
    }

    @Override
    public <ID> ID getKey() {
        return (ID) this.id;
    }
}
