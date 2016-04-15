package com.vaadin.tests.data.bean;

public class BeanWithReadOnlyField {
    private String readOnlyField;
    private String writableField;

    public String getWritableField() {
        return writableField;
    }

    public void setWritableField(String writableField) {
        this.writableField = writableField;
    }

    public String getReadOnlyField() {
        return readOnlyField;
    }
}
