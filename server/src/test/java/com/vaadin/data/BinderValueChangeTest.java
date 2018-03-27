/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.TextField;

/**
 * @author Vaadin Ltd
 *
 */
public class BinderValueChangeTest
        extends BinderTestBase<Binder<Person>, Person> {

    private AtomicReference<ValueChangeEvent<?>> event;

    private static class TestTextField extends TextField {
        @Override
        protected boolean setValue(String value, boolean userOriginated) {
            return super.setValue(value, userOriginated);
        }
    }

    @Before
    public void setUp() {
        binder = new Binder<>();
        item = new Person();
        event = new AtomicReference<>();
    }

    @Test
    public void unboundField_noEvents() {
        binder.addValueChangeListener(this::statusChanged);

        BindingBuilder<Person, String> binding = binder.forField(nameField);

        nameField.setValue("");
        assertNull(event.get());

        binding.bind(Person::getFirstName, Person::setFirstName);
        assertNull(event.get());
    }

    @Test
    public void setBean_unbound_noEvents() {
        binder.addValueChangeListener(this::statusChanged);

        assertNull(event.get());

        binder.setBean(item);

        assertNull(event.get());
    }

    @Test
    public void readBean_unbound_noEvents() {
        binder.addValueChangeListener(this::statusChanged);

        assertNull(event.get());

        binder.readBean(item);

        assertNull(event.get());
    }

    @Test
    public void setValue_unbound_singleEventOnSetValue() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);

        binder.addValueChangeListener(this::statusChanged);

        assertNull(event.get());
        nameField.setValue("foo");
        verifyEvent(nameField);
    }

    @Test
    public void setValue_bound_singleEventOnSetValue() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);
        binder.setBean(item);

        binder.addValueChangeListener(this::statusChanged);

        assertNull(event.get());
        nameField.setValue("foo");
        verifyEvent(nameField);
    }

    @Test
    public void userOriginatedUpdate_unbound_singleEventOnSetValue() {
        TestTextField field = new TestTextField();

        binder.forField(field).bind(Person::getFirstName, Person::setFirstName);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);

        binder.addValueChangeListener(this::statusChanged);

        assertNull(event.get());
        field.setValue("foo", true);
        verifyEvent(field, true);
    }

    @Test
    public void addListenerFirst_bound_singleEventOnSetValue() {
        binder.addValueChangeListener(this::statusChanged);

        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);
        binder.setBean(item);

        assertNull(event.get());
        ageField.setValue(String.valueOf(1));
        verifyEvent(ageField);
    }

    private void verifyEvent(HasValue<?> field) {
        verifyEvent(field, false);
    }

    private void verifyEvent(HasValue<?> field, boolean isUserOriginated) {
        ValueChangeEvent<?> changeEvent = event.get();
        assertNotNull(changeEvent);
        assertEquals(field, changeEvent.getSource());
        assertEquals(field, changeEvent.getComponent());
        assertEquals(isUserOriginated, changeEvent.isUserOriginated());
    }

    private void statusChanged(ValueChangeEvent<?> evt) {
        assertNull(event.get());
        event.set(evt);
    }
}
