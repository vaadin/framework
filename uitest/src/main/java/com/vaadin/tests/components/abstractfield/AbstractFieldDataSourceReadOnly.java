package com.vaadin.tests.components.abstractfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.TextField;

public class AbstractFieldDataSourceReadOnly extends TestBase {

    private static class StateHolder {
        private ObjectProperty<String> textField = new ObjectProperty<>("");

        public ObjectProperty<String> getTextField() {
            return textField;
        }

        @SuppressWarnings("unused")
        public void setTextField(ObjectProperty<String> textField) {
            this.textField = textField;
        }

        public void buttonClicked() {
            textField.setReadOnly(true);
        }
    }

    @Override
    protected void setup() {
        final StateHolder stateHolder = new StateHolder();

        // Button
        Button button = new Button("Make data source read-only");
        button.addListener(event -> stateHolder.buttonClicked());

        // Input field
        TextField input = new TextField("Field");
        input.setPropertyDataSource(stateHolder.getTextField());

        addComponent(button);
        addComponent(input);
    }

    @Override
    protected String getDescription() {
        return "Read-only status changes in data sources are not rendered immediately";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5013;
    }

}
