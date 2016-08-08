package com.vaadin.tests.components.form;

import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.FormLayout;

public class FormFieldCaptionsStyles extends TestBase {

    @Override
    protected void setup() {

        setTheme("tests-tickets");

        FormLayout layout = new FormLayout();

        LegacyTextField field1 = new LegacyTextField("Red style");
        field1.setStyleName("ticket4997-red");
        layout.addComponent(field1);

        LegacyTextField field2 = new LegacyTextField("Blue style");
        field2.setStyleName("ticket4997-blue");
        layout.addComponent(field2);

        LegacyTextField field3 = new LegacyTextField("Red-Blue style");
        field3.addStyleName("ticket4997-red");
        field3.addStyleName("ticket4997-blue");
        layout.addComponent(field3);

        LegacyTextField field4 = new LegacyTextField("Disabled");
        field4.setEnabled(false);
        field4.addStyleName("foobar"); // no visible change, but points out
        // a regression #5377
        layout.addComponent(field4);

        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "The field captions should have the same style names as the field";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4997;
    }

}
