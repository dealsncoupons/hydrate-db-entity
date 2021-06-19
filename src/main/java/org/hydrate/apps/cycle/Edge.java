package org.hydrate.apps.cycle;

import org.hydrate.apps.support.Entity;

public class Edge<E extends Entity> {

    final E from;
    final E to;

    public Edge(E from, E to) {
        this.from = from;
        this.to = to;
    }
}
