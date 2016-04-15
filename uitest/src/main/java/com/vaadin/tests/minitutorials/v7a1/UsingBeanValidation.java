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

package com.vaadin.tests.minitutorials.v7a1;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Using%20Bean%20Validation
 * %20to%20validate%20input
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class UsingBeanValidation extends UI {
    class Person {

        @Size(min = 5, max = 50)
        private String name;

        @Min(0)
        @Max(100)
        private int age;

        // + constructor + setters + getters

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        private void setN() {
            // TODO Auto-generated method stub

        }
    }

    @Override
    protected void init(VaadinRequest request) {
        Person person = new Person("John", 26);
        BeanItem<Person> item = new BeanItem<Person>(person);

        TextField firstName = new TextField("First name",
                item.getItemProperty("name"));
        firstName.setImmediate(true);
        setContent(firstName);

        firstName.addValidator(new BeanValidator(Person.class, "name"));
    }

}
