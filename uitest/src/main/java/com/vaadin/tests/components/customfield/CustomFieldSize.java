package com.vaadin.tests.components.customfield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class CustomFieldSize extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.setSpacing(false);
        setContent(layout);
        layout.setWidth("50px");

        layout.addComponent(new TextField());

        layout.addComponent(new TestCustomField());
    }

    @Override
    protected String getTestDescription() {
        return "Any part of a TextField wrapped in a CustomField should not be cut off even when the dimensions of the TextField exceed those of the CustomField";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12482;
    }

    private static class TestCustomField extends CustomField<String> {

        private TextField field = new TextField();

        @Override
        protected Component initContent() {
            return field;
        }

        @Override
        public String getValue() {
            return field.getValue();
        }

        @Override
        protected void doSetValue(String value) {
            field.setValue(value);
        }
    }

}
