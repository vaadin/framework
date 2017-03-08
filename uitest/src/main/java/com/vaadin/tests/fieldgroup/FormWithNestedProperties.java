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
package com.vaadin.tests.fieldgroup;

import com.vaadin.annotations.PropertyId;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.TextField;

public class FormWithNestedProperties extends AbstractBeanFieldGroupTest {

    private Log log = new Log(5);

    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private TextField email = new TextField("Email");
    private TextField age = new TextField("Age");

    @PropertyId("address.streetAddress")
    private TextField streetAddress = new TextField("Street address");
    private NativeSelect country;

    private CheckBox deceased = new CheckBox("Deceased");

    @Override
    protected void setup() {
        super.setup();

        setFieldBinder(new BeanFieldGroup<>(Person.class));
        country = (NativeSelect) getFieldBinder().buildAndBind("country",
                "address.country", NativeSelect.class);
        getFieldBinder().bindMemberFields(this);
        addComponent(firstName);
        addComponent(lastName);
        addComponent(streetAddress);
        addComponent(country);
        addComponent(email);
        addComponent(age);
        addComponent(deceased);
        addComponent(getCommitButton());
        addComponent(getDiscardButton());
        addComponent(getShowBeanButton());

        getFieldBinder().setItemDataSource(new Person("First", "Last", "Email",
                52, Sex.FEMALE,
                new Address("street address", 01234, "City", Country.FINLAND)));

    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
