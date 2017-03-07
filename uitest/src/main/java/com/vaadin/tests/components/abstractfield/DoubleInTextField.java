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
package com.vaadin.tests.components.abstractfield;

import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.v7.data.util.MethodProperty;
import com.vaadin.v7.ui.TextField;

public class DoubleInTextField extends AbstractComponentDataBindingTest {

    @Override
    protected void createFields() {
        Person person = new Person("John", "Doe", "john@doe.com", 78, Sex.MALE,
                new Address("Dovestreet 12", 12233, "Johnston",
                        Country.SOUTH_AFRICA));

        TextField salary = new TextField("Vaadin 7 - TextField with Double");
        addComponent(salary);
        salary.setPropertyDataSource(
                new MethodProperty<Double>(person, "salaryDouble"));

        TextField salary6 = new TextField("Vaadin 6 - TextField with Double");
        addComponent(salary6);
        salary6.setPropertyDataSource(
                new MethodProperty<Double>(person, "salaryDouble"));
        salary6.setConverter(new Vaadin6ImplicitDoubleConverter());

    }

}
