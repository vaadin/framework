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
package com.vaadin.data;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationError;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.AbstractField;

/**
 * Book of Vaadin tests.
 * 
 * @author Vaadin Ltd
 *
 */
public class BinderBookOfVaadinTest {

    static class TextField extends AbstractField<String> {

        String value = "";

        @Override
        public String getValue() {
            return value;
        }

        @Override
        protected void doSetValue(String value) {
            this.value = value;
        }
    }

    private Binder<Person> binder;

    private TextField field;

    private Person person = new Person();

    @Before
    public void setUp() {
        binder = new Binder<>();
        field = new TextField();
    }

    @Test
    public void simpleEmailValidator() {
        binder.forField(field)
                // Explicit validator instance
                .withValidator(new EmailValidator(
                        "This doesn't look like a valid email address"))
                .bind(Person::getEmail, Person::setEmail);

        field.setValue("not-email");
        List<ValidationError<?>> errors = binder.validate();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("This doesn't look like a valid email address",
                errors.get(0).getMessage());
        Assert.assertEquals("This doesn't look like a valid email address",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@vaadin.com");
        errors = binder.validate();
        Assert.assertEquals(0, errors.size());
        Assert.assertNull(field.getErrorMessage());
    }

    @Test
    public void nameLengthTest() {
        binder.forField(field)
                // Validator defined based on a lambda and an error message
                .withValidator(name -> name.length() >= 3,
                        "Last name must contain at least three characters")
                .bind(Person::getLastName, Person::setLastName);

        field.setValue("a");
        List<ValidationError<?>> errors = binder.validate();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Last name must contain at least three characters",
                errors.get(0).getMessage());
        Assert.assertEquals("Last name must contain at least three characters",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("long last name");
        errors = binder.validate();
        Assert.assertEquals(0, errors.size());
        Assert.assertNull(field.getErrorMessage());
    }

    @Test
    public void chainedEmailValidator() {
        binder.forField(field)
                // Explicit validator instance
                .withValidator(new EmailValidator(
                        "This doesn't look like a valid email address"))
                .withValidator(email -> email.endsWith("@acme.com"),
                        "Only acme.com email addresses are allowed")
                .bind(Person::getEmail, Person::setEmail);

        field.setValue("not-email");
        List<ValidationError<?>> errors = binder.validate();
        Assert.assertEquals(2, errors.size());
        Assert.assertEquals("This doesn't look like a valid email address",
                errors.get(0).getMessage());
        Assert.assertEquals("Only acme.com email addresses are allowed",
                errors.get(1).getMessage());
        Assert.assertEquals("This doesn't look like a valid email address",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@vaadin.com");
        errors = binder.validate();
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals("Only acme.com email addresses are allowed",
                errors.get(0).getMessage());
        Assert.assertEquals("Only acme.com email addresses are allowed",
                ((AbstractErrorMessage) field.getErrorMessage()).getMessage());

        field.setValue("abc@acme.com");
        errors = binder.validate();
        Assert.assertEquals(0, errors.size());
        Assert.assertNull(field.getErrorMessage());
    }
}
