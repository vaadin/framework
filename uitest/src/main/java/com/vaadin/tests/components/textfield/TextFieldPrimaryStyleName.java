package com.vaadin.tests.components.textfield;

import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class TextFieldPrimaryStyleName extends TestBase {

    @Override
    protected void setup() {
        final LegacyTextField field = new LegacyTextField();
        field.setPrimaryStyleName("my-textfield");
        addComponent(field);

        addComponent(new Button("Change primary style name",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        field.setPrimaryStyleName("my-dynamic-textfield");
                    }
                }));

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
