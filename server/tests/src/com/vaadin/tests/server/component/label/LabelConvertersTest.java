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
package com.vaadin.tests.server.component.label;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.Label;
import com.vaadin.util.CurrentInstance;

public class LabelConvertersTest {
    @Before
    public void clearExistingThreadLocals() {
        // Ensure no previous test left some thread locals hanging
        CurrentInstance.clearAll();
    }

    @Test
    public void testLabelSetDataSourceLaterOn() {
        Person p = Person.createTestPerson1();
        Label l = new Label("My label");
        assertEquals("My label", l.getValue());
        assertNull(l.getConverter());
        l.setPropertyDataSource(new MethodProperty<String>(p, "firstName"));
        assertEquals(p.getFirstName(), l.getValue());
        p.setFirstName("123");
        assertEquals("123", l.getValue());
    }

    @Test
    public void testIntegerDataSource() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));
        Label l = new Label("Foo");
        Property ds = new MethodProperty<Integer>(Person.createTestPerson1(),
                "age");
        l.setPropertyDataSource(ds);
        assertEquals(String.valueOf(Person.createTestPerson1().getAge()),
                l.getValue());
    }

    @Test
    public void testSetValueWithDataSource() {
        try {
            MethodProperty<String> property = new MethodProperty<String>(
                    Person.createTestPerson1(), "firstName");
            Label l = new Label(property);
            l.setValue("Foo");
            fail("setValue should throw an exception when a data source is set");
        } catch (Exception e) {
        }

    }

    @Test
    public void testLabelWithoutDataSource() {
        Label l = new Label("My label");
        assertEquals("My label", l.getValue());
        assertNull(l.getConverter());
        assertNull(l.getPropertyDataSource());
        l.setValue("New value");
        assertEquals("New value", l.getValue());
        assertNull(l.getConverter());
        assertNull(l.getPropertyDataSource());
    }
}
