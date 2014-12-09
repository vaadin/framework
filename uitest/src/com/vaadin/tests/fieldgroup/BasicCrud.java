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
package com.vaadin.tests.fieldgroup;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("valo")
public class BasicCrud extends UI {

    private Form form;

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout main = new VerticalLayout();
        main.setMargin(true);
        main.setSpacing(true);
        final Table t = new Table();
        // t.setSelectionMode(SelectionMode.SINGLE);
        t.setSelectable(true);

        PersonContainer c = PersonContainer.createWithTestData();

        c.addBean(new Person("first", "Last", "email", "phone", "street",
                12332, "Turku"));
        c.addBean(new Person("Foo", "Bar", "me@some.where", "123",
                "homestreet 12", 10000, "Glasgow"));
        t.setContainerDataSource(c);
        // t.removeColumn("address");
        // t.setColumnOrder("firstName", "lastName", "email", "phoneNumber",
        // "address.streetAddress", "address.postalCode", "address.city");
        t.setVisibleColumns("firstName", "lastName", "email", "phoneNumber",
                "address.streetAddress", "address.postalCode", "address.city");

        // t.addSelectionChangeListener(new SelectionChangeListener() {
        // @Override
        // public void selectionChange(SelectionChangeEvent event) {
        // form.edit((Person) t.getSelectedRow());
        // }
        // });
        t.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                form.edit((Person) t.getValue());
            }
        });

        form = new Form();

        t.setSizeFull();

        main.setSizeFull();
        main.addComponent(t);
        main.addComponent(form);
        main.setExpandRatio(t, 1);
        setContent(main);
    }

    public static class Form extends HorizontalLayout {
        private TextField firstName = new TextField("First name");
        private TextField lastName = new TextField("Last name");
        private TextField email = new TextField("E-mail");
        @PropertyId("address.streetAddress")
        private TextField streetAddress = new TextField("Street address");
        @PropertyId("address.postalCode")
        private TextField postalCode = new TextField("Postal code");

        BeanFieldGroup<Person> fieldGroup = new BeanFieldGroup<Person>(
                Person.class);

        public Form() {
            setSpacing(true);
            setId("form");
            fieldGroup.bindMemberFields(this);

            // Stupid integer binding
            postalCode.setNullRepresentation("");
            addComponents(firstName, lastName, email, streetAddress, postalCode);
        }

        public void edit(Person p) {
            fieldGroup.setItemDataSource(p);
        }
    }
}
