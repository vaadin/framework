package com.vaadin.data.provider.hierarchical;

import java.io.Serializable;

public class Node implements Serializable {

    private final Node parent;
    private final int number;

    public Node(int number) {
        this(null, number);
    }

    public Node(Node parent, int number) {
        this.parent = parent;
        this.number = number;
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
