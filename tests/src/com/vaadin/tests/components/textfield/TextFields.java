package com.vaadin.tests.components.textfield;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

public class TextFields extends ComponentTestCase<TextField> {

    @Override
    protected void setup() {
        super.setup();

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

    @Override
    protected String getDescription() {
        return "A generic test for TextFields in different configurations";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<Component> createActions() {
        ArrayList<Component> actions = new ArrayList<Component>();

        CheckBox errorIndicators = new CheckBox("Error indicators",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Button b = event.getButton();
                        boolean enabled = (Boolean) b.getValue();
                        setErrorIndicators(enabled);

                    }
                });

        CheckBox required = new CheckBox("Required",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Button b = event.getButton();
                        boolean enabled = (Boolean) b.getValue();
                        setRequired(enabled);
                    }
                });

        CheckBox enabled = new CheckBox("Enabled", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                Button b = event.getButton();
                boolean enabled = (Boolean) b.getValue();
                setEnabled(enabled);
            }
        });

        CheckBox readonly = new CheckBox("Readonly",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Button b = event.getButton();
                        boolean enabled = (Boolean) b.getValue();
                        setReadOnly(enabled);
                    }
                });

        errorIndicators.setValue(Boolean.FALSE);
        required.setValue(Boolean.FALSE);
        readonly.setValue(Boolean.FALSE);
        enabled.setValue(Boolean.TRUE);

        errorIndicators.setImmediate(true);
        required.setImmediate(true);
        readonly.setImmediate(true);
        enabled.setImmediate(true);

        actions.add(errorIndicators);
        actions.add(required);
        actions.add(readonly);
        actions.add(enabled);

        return actions;
    }

}
