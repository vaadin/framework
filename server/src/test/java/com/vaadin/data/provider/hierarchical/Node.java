package com.vaadin.data.provider.hierarchical;

import java.io.Serializable;

public class Node implements Serializable {

    private static int counter = 0;

    private final Node parent;
    private final int number;

    public Node() {
        this(null);
    }

    public Node(Node parent) {
        this.parent = parent;
        this.number = counter++;
    }

    public Node getParent() {
        return parent;
    }

    public int getNumber() {
        return number;
    }

    public String toString() {
        return number + (parent != null ? " [parent: " + parent + "]" : "");
    }
}
