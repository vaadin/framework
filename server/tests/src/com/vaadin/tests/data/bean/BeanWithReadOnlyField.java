package com.vaadin.tests.data.bean;

public class BeanWithReadOnlyField {
    private String readOnlyField;
    private String writableField;

    public String getReadableField() {
        return writableField;
    }

    public void setReadableField(String readableField) {
        readOnlyField = readableField;
    }

    public String getReadOnlyField() {
        return readOnlyField;
    }
}
