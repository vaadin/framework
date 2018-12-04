package com.vaadin.data.provider.bov;

import java.io.Serializable;

public class Person implements Serializable {
    private String name;
    private final int born;

    public Person(String name, int born) {
        this.name = name;
        this.born = born;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBorn() {
        return born;
    }

    @Override
    public String toString() {
        return name + "(" + born + ")";
    }
}
