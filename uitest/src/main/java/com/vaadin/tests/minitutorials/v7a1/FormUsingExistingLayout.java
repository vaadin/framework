package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class FormUsingExistingLayout extends AbstractTestUI {

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
        private TextField firstName = new TextField("First name");
        private TextField lastName = new TextField("Last name");

        // The name of the property is by default the name of the member field,
        // but it can be redefined with the @PropertyId annotation
        @PropertyId("message")
        private TextArea messageField = new TextArea("Your message");

        public MyFormLayout() {
            // Set up the GridLayout
            super(2, 3);
            setSpacing(true);

            // Add the (currently unbound) fields
            addComponent(firstName);
            addComponent(lastName);

            addComponent(messageField, 0, 1, 1, 1);
            messageField.setWidth("100%");
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        // Create the layout
        MyFormLayout myFormLayout = new MyFormLayout();

        // Create a field group and use it to bind the fields in the layout
        FieldGroup fieldGroup = new FieldGroup(new BeanItem<Notice>(new Notice(
                "John", "Doe", "")));
        fieldGroup.bindMemberFields(myFormLayout);

        addComponent(myFormLayout);
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
