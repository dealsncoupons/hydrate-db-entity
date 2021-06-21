package org.hydrate.apps.support;

import java.util.Map;

public interface Entity {

    EntityInfo info();

    Map<String, Object> extractValues();

    <V> void set(String field, V value);

    <V> V get(String field);

    <ID> ID getKey();
}
