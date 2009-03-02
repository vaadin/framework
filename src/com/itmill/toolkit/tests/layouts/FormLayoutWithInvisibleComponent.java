package com.itmill.toolkit.tests.layouts;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.FormLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class FormLayoutWithInvisibleComponent extends TestBase {

    private TextField messages;

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
        CheckBox control = new CheckBox("Messages On/Off",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        messages.setVisible(event.getButton().booleanValue());
                        messages.setRequired(true);
                        messages.setCaption("Messages visible");
                    }

                });
        control.setImmediate(true);
        formLayout.addComponent(control);

        messages = new TextField("Messages hidden");
        messages.setRows(10);
        messages.setColumns(40);
        messages.setVisible(false);
        messages.setEnabled(false);
        formLayout.addComponent(messages);

        addComponent(formLayout);
    }

}
