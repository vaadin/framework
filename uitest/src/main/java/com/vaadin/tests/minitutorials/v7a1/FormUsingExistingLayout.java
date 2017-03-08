/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.annotations.PropertyId;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

public class FormUsingExistingLayout extends AbstractReindeerTestUI {

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
        FieldGroup fieldGroup = new FieldGroup(
                new BeanItem<>(new Notice("John", "Doe", "")));
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
