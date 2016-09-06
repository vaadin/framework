package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;

public class FormLayoutWithInvisibleComponent extends TestBase {

    private TextArea messages;

    @Override
    protected String getDescription() {
        return "There is an initial invisible text field below the checkbox. Checking the checkbox should show the field as a textarea (40x10) and also show its caption(\"Messages visible\") and a required error (*).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2706;
    }

    @Override
    protected void setup() {
        FormLayout formLayout = new FormLayout();
        CheckBox control = new CheckBox("Messages On/Off");
        control.addValueChangeListener(event -> {
            messages.setVisible(event.getValue());
            messages.setRequired(true);
            messages.setCaption("Messages visible");
        });
        control.setImmediate(true);
        formLayout.addComponent(control);

        messages = new TextArea("Messages hidden");
        messages.setRows(10);
        messages.setWidth("40em");
        messages.setVisible(false);
        messages.setEnabled(false);
        formLayout.addComponent(messages);

        addComponent(formLayout);
    }

}
