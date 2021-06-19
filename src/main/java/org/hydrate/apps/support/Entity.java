package org.hydrate.apps.support;

import java.util.Map;

public interface Entity {

    EntityInfo info();

    Map<String, Object> extractValues();

    <V> void set(String pk, V pkValue);

    <V> V get(String name);

    <ID> ID getKey();
}
