package com.vaadin.tests.data.bean;

public enum Country {

    FINLAND("Finland"), SWEDEN("Sweden"), USA("USA"), RUSSIA("Russia"), HOLLAND(
            "Holland"), SOUTH_AFRICA("South Africa");

    private String name;

    private Country(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
