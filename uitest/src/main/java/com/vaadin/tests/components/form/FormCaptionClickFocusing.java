package com.vaadin.tests.components.form;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

public class FormCaptionClickFocusing extends TestBase {

    @Override
    protected void setup() {
        FormLayout layout = new FormLayout();

        TextField field = new TextField("Field 1");
        layout.addComponent(field);

        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "Formlayout should focus the field if the layout is clicked and the field is focusable.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4567;
    }

}
