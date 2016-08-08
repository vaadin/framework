package com.vaadin.tests.validation;

import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Form;

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
        LegacyTextField tf = new LegacyTextField("A read only field");
        tf.setReadOnly(true);
        tf.setRequired(true);
        addComponent(tf);

        Form f = new Form();
        LegacyTextField tf2 = new LegacyTextField("A field in a read only form");
        tf2.setRequired(true);
        f.addField("Field-1", tf2);
        f.setReadOnly(true);
        addComponent(f);
    }
}
