package com.vaadin.tests.components.textfield;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.terminal.UserError;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

public class TextFields extends TestBase {

    private TextField textFields[] = new TextField[20];

    @Override
    protected void setup() {
        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setSpacing(true);
        actionLayout.setMargin(true);
        for (Component c : createActions()) {
            actionLayout.addComponent(c);
        }
        addComponent(actionLayout);

        int index = 0;

        textFields[index] = createTextField("TextField 100% wide");
        textFields[index].setWidth("100%");
        addComponent(textFields[index++]);

        textFields[index] = createTextField(null,
                "TextField 100% wide, no caption");
        textFields[index].setWidth("100%");
        addComponent(textFields[index++]);

        textFields[index] = createTextField("TextField auto wide");
        addComponent(textFields[index++]);

        textFields[index] = createTextField("TextField with input prompt");
        textFields[index].setInputPrompt("Please enter a value");
        addComponent(textFields[index++]);

        textFields[index] = createTextField("100px wide textfield");
        textFields[index].setWidth("100px");
        addComponent(textFields[index++]);

        textFields[index] = createTextField("150px wide, 120px high textfield");
        textFields[index].setWidth("150px");
        textFields[index].setHeight("120px");
        addComponent(textFields[index++]);

        textFields[index] = createTextField("50px high textfield");
        textFields[index].setHeight("50px");
        addComponent(textFields[index++]);

        textFields[index] = createTextField(null, "No caption");
        addComponent(textFields[index++]);

        textFields[index] = createTextField(null, "No caption and input prompt");
        textFields[index].setInputPrompt("Enter a value");
        addComponent(textFields[index++]);

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

        errorIndicators.setValue(new Boolean(false));
        required.setValue(new Boolean(false));
        readonly.setValue(new Boolean(false));
        enabled.setValue(new Boolean(true));

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

    protected void setRequired(boolean on) {

        for (TextField tf : textFields) {
            if (tf == null) {
                continue;
            }

            tf.setRequired(on);
        }

    }

    protected void setErrorIndicators(boolean on) {
        for (TextField tf : textFields) {
            if (tf == null) {
                continue;
            }

            if (on) {
                tf.setComponentError(new UserError("It failed!"));
            } else {
                tf.setComponentError(null);

            }
        }

    }

    protected void setEnabled(boolean on) {
        for (TextField tf : textFields) {
            if (tf == null) {
                continue;
            }

            tf.setEnabled(on);
        }

    }

    protected void setReadOnly(boolean on) {
        for (TextField tf : textFields) {
            if (tf == null) {
                continue;
            }

            tf.setReadOnly(on);
        }

    }

}
