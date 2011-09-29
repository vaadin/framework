package com.vaadin.tests.validation;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;

public class RequiredIndicatorForReadOnly extends TestBase {

    @Override
    protected String getDescription() {
        return "Required flags should not be visible when a component is in read-only mode or inside a read-only form";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2465;
    }

    @Override
    protected void setup() {
        TextField tf = new TextField("A read only field");
        tf.setReadOnly(true);
        tf.setRequired(true);
        addComponent(tf);

        Form f = new Form();
        TextField tf2 = new TextField("A field in a read only form");
        tf2.setRequired(true);
        f.addField("Field-1", tf2);
        f.setReadOnly(true);
        addComponent(f);
    }
}
