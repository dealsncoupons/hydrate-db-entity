package org.hydrate.apps.entity;

import org.hydrate.apps.support.Entity;
import org.hydrate.apps.support.EntityInfo;
import org.hydrate.apps.support.EntityMetadata;

import java.time.LocalDate;
import java.util.*;

public class Player implements IPlayer, Entity {

    public static final String TABLE_NAME = "tbl_player";

    private UUID id;
    private String nameAlias;
    private LocalDate dateJoined;
    private Collection<IAssignment> assignments = new ArrayList<>();

    @Override
    public UUID id() {
        return this.id;
    }

    @Override
    public String nameAlias() {
        return this.nameAlias;
    }

    @Override
    public LocalDate dateJoined() {
        return this.dateJoined;
    }

    @Override
    public Collection<IAssignment> assignments() {
        return new ArrayList<>(this.assignments);
    }

    @Override
    public EntityInfo info() {
        return EntityMetadata.playerInfo();
    }

    @Override
    public Map<String, Object> extractValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("id", this.id);
        values.put("name_alias", this.nameAlias);
        values.put("date_joined", this.dateJoined);
        return values;
    }

    @Override
    public <V> void set(String field, V value) {
        switch (field) {
            case "id":
                this.id = (UUID) value;
                break;
            case "nameAlias":
                this.nameAlias = (String) value;
                break;
            case "dateJoined":
                this.dateJoined = (LocalDate) value;
                break;
            case "assignments":
                this.assignments = (Collection<IAssignment>) value;
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
                return (V) this.nameAlias;
            case "status":
                return (V) this.dateJoined;
            case "assignments":
                return (V) this.assignments;
            default:
                throw new RuntimeException("This field is not yet mapped through the 'set' method");
        }
    }

    @Override
    public <ID> ID getKey() {
        return (ID) this.id;
    }
}
