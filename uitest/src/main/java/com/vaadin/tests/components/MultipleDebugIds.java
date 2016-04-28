package com.vaadin.tests.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;

public class MultipleDebugIds extends TestBase {

    @Override
    protected String getDescription() {
        return "An exception should be thrown if the same debugId is assigned to several components";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2796;
    }

    @Override
    protected void setup() {
        TextField textField = new TextField();
        TextField textField2 = new TextField();
        Button button = new Button();
        Button button2 = new Button();
        textField.setId("textfield");
        button.setId("button");
        textField2.setId("textfield2");
        button2.setId("textfield");

        addComponent(textField);
        addComponent(textField2);
        addComponent(button);
        addComponent(button2);
    }

}
