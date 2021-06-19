package org.hydrate.apps.cycle;

import org.hydrate.apps.repo.exception.EntityRelationCycle;
import org.hydrate.apps.support.Entity;
import org.hydrate.apps.support.EntityInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Cycle<E extends Entity> {

    private final static Logger LOGGER = LoggerFactory.getLogger(Cycle.class);

    List<Vertex<E>> vertices = new ArrayList<>();

    public void addEdge(E from, E to) {
        Vertex<E> vertex = new Vertex<>();
        //check for potential cycle
        boolean cycle = vertices.stream().anyMatch(v ->
                v.edges.stream().anyMatch(
                        e -> (e.from == to && e.to == from)));
        if (cycle) {
            throw new EntityRelationCycle("Entity " + from + " and " + to + " have a cycle dependency.\n" +
                    "Save them individually and then update " + from + " with " + to);
        }
        vertex.addEdge(new Edge<>(from, to));
        this.vertices.add(vertex);
    }

    public void checkCycle(E root) {
        EntityInfo info = root.info();
        info.getColumns().stream().filter(field -> field.isRelational).forEach(field -> {
            if (!field.isCollection) {
                E manyToOne = root.get(field.name);
                if (manyToOne != null) {
                    LOGGER.info("create edge from entity {} to entity {}", root, manyToOne);
                    this.addEdge(root, manyToOne);
                    checkCycle(manyToOne);
                }
            } else {
                Collection<E> collection = root.get(field.name);
                collection.forEach(this::checkCycle);
            }
        });
    }
}
