package com.vaadin.tests.minitutorials;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.Caption;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class FormUsingExistingLayout extends AbstractTestRoot {

    public static class Notice {
        String firstName;
        String lastName;
        String message;

        public Notice(String firstName, String lastName, String message) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.message = message;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public static class MyFormLayout extends GridLayout {
        private TextField firstName;
        private TextField lastName;

        // The name of the property is by default the name of the member field,
        // but it can be redefined with the @PropertyId annotation
        @PropertyId("message")
        // The field caption is by default derived from the property id, but can
        // be redefined with the @Caption annotation
        @Caption("Your message")
        private TextArea messageField;

        public MyFormLayout(Item item) {
            // Set up the GridLayout
            super(2, 3);
            setSpacing(true);

            // Create a field group
            final FieldGroup fieldGroup = new FieldGroup(item);

            // Create and bind fields for the item and inject the fields to this
            fieldGroup.buildAndBindMemberFields(this);

            // The fields have been initialized and can be added to the layout
            addComponent(firstName);
            addComponent(lastName);

            addComponent(messageField, 0, 1, 1, 1);
            messageField.setWidth("100%");
        }

    }

    @Override
    protected void setup(WrappedRequest request) {
        addComponent(new MyFormLayout(new BeanItem<Notice>(new Notice("John",
                "Doe", ""))));
    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial for https://vaadin.com/wiki/-/wiki/Main/Creating%20a%20form%20using%20an%20existing%20layout";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
