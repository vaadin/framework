package com.vaadin.tests.data.bean;

public enum TestEnum {
    ONE("1"), TWO("2");

    private String id;

    private TestEnum(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
