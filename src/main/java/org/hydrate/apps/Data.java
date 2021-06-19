package org.hydrate.apps;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    private static Data instance;

    private Map<String, Map<UUID, Map<Object, Object>>> cache = new ConcurrentHashMap<>();

    private Data() {
    }

    private static Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    public static <V> V get(String tableName, UUID pkId, String columnName) {
        return getInstance().getValue(tableName, pkId, columnName);
    }

    public static <V> void put(String tableName, UUID pkId, String columnName, V value) {
        getInstance().putValue(tableName, pkId, columnName, value);
    }

    private Map<Object, Object> record(String tableName, UUID pkId) {
        if (table(tableName).get(pkId) == null) {
            table(tableName).put(pkId, new ConcurrentHashMap<>());
        }
        return table(tableName).get(pkId);
    }

    private Map<UUID, Map<Object, Object>> table(String tableName) {
        if (cache.get(tableName) == null) {
            cache.put(tableName, new ConcurrentHashMap<>());
        }
        return cache.get(tableName);
    }

    private <V> V getValue(String tableName, UUID pkId, String columnName) {
        Map<Object, Object> record = record(tableName, pkId);
        return (V) record.get(columnName);
    }

    private <V> V putValue(String tableName, UUID pkId, String columnName, V value) {
        Map<Object, Object> record = record(tableName, pkId);
        return (V) record.put(columnName, value);
    }
}
