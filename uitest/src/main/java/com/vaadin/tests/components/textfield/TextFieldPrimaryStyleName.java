package com.vaadin.tests.components.textfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.TextField;

public class TextFieldPrimaryStyleName extends TestBase {

    @Override
    protected void setup() {
        final TextField field = new TextField();
        field.setPrimaryStyleName("my-textfield");
        addComponent(field);

        addComponent(new Button("Change primary style name",
                event -> field.setPrimaryStyleName("my-dynamic-textfield")));
    }

    @Override
    protected String getDescription() {
        return "Textfield should support setting the primary stylename both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9896;
    }

}
