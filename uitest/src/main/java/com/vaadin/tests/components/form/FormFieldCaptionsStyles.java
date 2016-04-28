package com.vaadin.tests.components.form;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

public class FormFieldCaptionsStyles extends TestBase {

    @Override
    protected void setup() {

        setTheme("tests-tickets");

        FormLayout layout = new FormLayout();

        TextField field1 = new TextField("Red style");
        field1.setStyleName("ticket4997-red");
        layout.addComponent(field1);

        TextField field2 = new TextField("Blue style");
        field2.setStyleName("ticket4997-blue");
        layout.addComponent(field2);

        TextField field3 = new TextField("Red-Blue style");
        field3.addStyleName("ticket4997-red");
        field3.addStyleName("ticket4997-blue");
        layout.addComponent(field3);

        TextField field4 = new TextField("Disabled");
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
