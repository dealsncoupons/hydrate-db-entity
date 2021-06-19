package org.hydrate.apps.support;

import org.hydrate.apps.repo.exception.EntityInstanceCreationFailed;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class EntityInfo {

    private final String tableName;
    private final Class<?> type;
    private final List<ColumnInfo> columns = new LinkedList<>();

    public EntityInfo(String tableName, Class<?> type) {
        this.tableName = tableName;
        this.type = type;
    }

    public void addColumn(ColumnInfo column) {
        this.columns.add(column);
    }

    public String getTableName() {
        return tableName;
    }

    public Class<?> getType() {
        return type;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public Entity newInstance() {
        try {
            return (Entity) getType().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new EntityInstanceCreationFailed("Could not create a new entity instance", e);
        }
    }
}
