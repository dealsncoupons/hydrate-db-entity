package org.hydrate.apps.support;

import java.util.HashMap;

public class LocalCache<ID> extends HashMap<ID, Entity> {

    public void addItem(ID key, Entity entity) {
        if (!containsKey(key)) {
            final Entity o = put(key, entity);
            assert o == entity;
        }
    }

    public Entity getItem(ID key) {
        if (containsKey(key)) {
            return get(key);
        }
        return null;
    }

    public <V> void putItem(ID key, Entity entity) {
        final Entity o = put(key, entity);
        assert o == entity;
    }

    public <V> void dropItem(ID key) {
        remove(key);
    }

    public boolean hasItem(ID key) {
        return containsKey(key);
    }
}
