package com.vaadin.tests.components.textfield;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.TextField;

public class TextFields extends ComponentTestCase<TextField> {

    @Override
    protected Class<TextField> getTestClass() {
        return TextField.class;
    }

    @Override
    protected void initializeComponents() {
        TextField tf;

        tf = createTextField("TextField 100% wide");
        tf.setWidth("100%");
        addTestComponent(tf);

        tf = createTextField(null, "TextField 100% wide, no caption");
        tf.setWidth("100%");
        addTestComponent(tf);

        tf = createTextField("TextField auto wide");
        addTestComponent(tf);

        tf = createTextField("TextField with input prompt");
        tf.setInputPrompt("Please enter a value");
        addTestComponent(tf);

        tf = createTextField("100px wide textfield");
        tf.setWidth("100px");
        addTestComponent(tf);

        tf = createTextField("150px wide, 120px high textfield");
        tf.setWidth("150px");
        tf.setHeight("120px");
        addTestComponent(tf);

        tf = createTextField("50px high textfield");
        tf.setHeight("50px");
        addTestComponent(tf);

        tf = createTextField(null, "No caption");
        addTestComponent(tf);

        tf = createTextField(null, "No caption and input prompt");
        tf.setInputPrompt("Enter a value");
        addTestComponent(tf);

    }

    private TextField createTextField(String caption, String value) {
        TextField tf = new TextField(caption);
        tf.setValue(value);

        return tf;
    }

    private TextField createTextField(String caption) {
        return createTextField(caption, "");
    }

}
