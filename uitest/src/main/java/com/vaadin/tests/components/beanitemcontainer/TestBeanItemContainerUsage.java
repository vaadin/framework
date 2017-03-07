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
package com.vaadin.tests.components.beanitemcontainer;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

public class TestBeanItemContainerUsage extends TestBase {

    @Override
    protected String getDescription() {
        return "A test for the BeanItemContainer. The table should contain three persons and show their first and last names and their age.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 1061;
    }

    @Override
    protected void setup() {
        Table t = new Table("Table containing Persons");
        t.setPageLength(5);
        t.setWidth("100%");
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Jones", "Birchman", 35));
        persons.add(new Person("Marc", "Smith", 30));
        persons.add(new Person("Greg", "Sandman", 75));

        BeanItemContainer<Person> bic = new BeanItemContainer<>(persons);
        t.setContainerDataSource(bic);

        addComponent(t);
    }

    public static class Person {
        private String firstName;
        private String lastName;
        private int age;

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

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Person(String firstName, String lastName, int age) {
            super();
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

    }
}
