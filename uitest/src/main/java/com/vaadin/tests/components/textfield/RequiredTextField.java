package com.vaadin.tests.components.textfield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.TextField;

/**
 * Test for required text field.
 *
 * @author Vaadin Ltd
 */
public class RequiredTextField extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final TextField field = new TextField();

        addComponent(field);

        Button button = new Button("Set/unset required",
                event -> field.setRequired(!field.isRequired()));
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Add .v-required style when setRequired() is used";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10201;
    }
}
