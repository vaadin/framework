/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.proto;

import java.util.Date;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.communication.data.typed.CollectionDataSource;
import com.vaadin.server.communication.data.typed.DataSource;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.proto.ListBox;
import com.vaadin.ui.proto.TypedForm;

public class TypedFormUI extends AbstractTestUI {

    public static class MyPerson {

        private String firstName;
        private String lastName;

        private Date birthDate;

        public MyPerson(String firstName, String lastName, Date birthDate) {
            setFirstName(firstName);
            setLastName(lastName);
            setBirthDate(birthDate);
        }

        public MyPerson() {
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

        public Date getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(Date birthDate) {
            this.birthDate = birthDate;
        }

        @Override
        public String toString() {
            return getLastName() + ", " + getFirstName();
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        DataSource<MyPerson> data = new CollectionDataSource<MyPerson>();
        final TypedForm<MyPerson> form = new TypedForm<MyPerson>(
                MyPerson.class, data) {

            @Override
            protected void save() {
                super.save();

                // We want to disable the form when saving.
                clear();
            }
        };

        form.addComponent(form.getButtonLayout());

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);

        final ListBox<MyPerson> listBox = new ListBox<MyPerson>(data);
        layout.addComponent(listBox);
        layout.addComponent(new Button("Edit", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (listBox.getSelected() != null) {
                    form.edit(listBox.getSelected());
                }
            }
        }));
        Button newButton = new Button("New MyPerson", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                form.edit(new MyPerson());
            }
        });
        layout.addComponent(newButton);

        layout.setWidth("100%");
        layout.setComponentAlignment(newButton, Alignment.MIDDLE_RIGHT);
        layout.setExpandRatio(newButton, 1.0f);

        addComponent(layout);
        addComponent(form);
    }
}