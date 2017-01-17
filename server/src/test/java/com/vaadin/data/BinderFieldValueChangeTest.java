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

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.tests.data.bean.Person;

/**
 * @author Vaadin Ltd
 *
 */
public class BinderFieldValueChangeTest
        extends BinderTestBase<Binder<Person>, Person> {

    private AtomicReference<FieldValueChangeEvent<Person>> event;

    @Before
    public void setUp() {
        binder = new Binder<>();
        item = new Person();
        event = new AtomicReference<>();
    }

    @Test
    public void bindBinding_unbound_noEvents() {
        binder.addFieldValueChangeListener(this::statusChanged);

        BindingBuilder<Person, String> binding = binder.forField(nameField);

        nameField.setValue("a");
        Assert.assertNull(event.get());

        binding.bind(Person::getFirstName, Person::setFirstName);
        Assert.assertNull(event.get());
    }

    @Test
    public void bindBinder_unbound_noChangeEventsWhenBound() {
        binder.addFieldValueChangeListener(this::statusChanged);

        Assert.assertNull(event.get());

        item.setFirstName("a");
        binder.setBean(item);

        Assert.assertNull(event.get());
    }

    @Test
    public void removeBean_bound_singleEventWhenUnBound() {
        binder.setBean(item);

        binder.addFieldValueChangeListener(this::statusChanged);

        Assert.assertNull(event.get());
        binder.removeBean();
        Assert.assertNull(event.get());
    }

    @Test
    public void bindBinder_setBean_noChangeEvents() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);

        binder.addFieldValueChangeListener(this::statusChanged);

        item.setFirstName("a");
        binder.setBean(item);

        Assert.assertNull(event.get());
    }

    @Test
    public void setValue_bound_singleEventOnSetValue() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.setBean(item);

        binder.addFieldValueChangeListener(this::statusChanged);

        Assert.assertNull(event.get());
        nameField.setValue("foo");
        verifyEvent();
    }

    @Test
    public void setValue_severalBoundFieldsAndBoundBinder_eventsPerFieldUpdate() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.forField(ageField)
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);
        binder.setBean(item);

        binder.addFieldValueChangeListener(this::statusChanged);

        Assert.assertNull(event.get());
        nameField.setValue("foo");
        verifyEvent();

        event.set(null);
        ageField.setValue(String.valueOf(2));
        verifyEvent(ageField);
    }

    @Test
    public void readBean_hasBindings_singleEventOnChange() {
        binder.forField(nameField).bind(Person::getFirstName,
                Person::setFirstName);
        binder.addFieldValueChangeListener(this::statusChanged);
        Assert.assertNull(event.get());
        binder.readBean(item);
        Assert.assertNull(event.get());

        nameField.setValue("foo");
        verifyEvent();
    }

    private void verifyEvent() {
        verifyEvent(nameField);
    }

    private void verifyEvent(HasValue<?> field) {
        Assert.assertEquals(binder, event.get().getBinder());
        Assert.assertEquals(field, event.get().getSource());
    }

    private void statusChanged(FieldValueChangeEvent<Person> evt) {
        Assert.assertNull(event.get());
        event.set(evt);
    }
}
