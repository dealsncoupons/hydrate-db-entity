package org.hydrate.apps.cycle;

import org.hydrate.apps.support.Entity;

import java.util.ArrayList;
import java.util.List;

public class Vertex<E extends Entity> {

    final List<Edge<E>> edges = new ArrayList<>();

    public void addEdge(Edge<E> edge) {
        this.edges.add(edge);
    }
}
