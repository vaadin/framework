package com.vaadin.tests.widgetset.client;

import java.io.Serializable;

public class SimpleTestBean implements Serializable {
    private int value;

    public SimpleTestBean() {
        this(0);
    }

    public SimpleTestBean(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SimpleTestBean(" + value + ")";
    }

    @Override
    public int hashCode() {
        // Implement hash code to get consistent HashSet.toString
        return value;
    }
}
