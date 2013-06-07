package com.vaadin.tests.data.bean;

public enum AnotherTestEnum {
    ONE("ONE"), TWO("TWO");

    private String id;

    private AnotherTestEnum(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
