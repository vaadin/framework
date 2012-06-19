/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.server.component.label;

import junit.framework.TestCase;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Label;

public class LabelConverters extends TestCase {

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

    public void testIntegerDataSource() {
        Application.setCurrentApplication(new Application());
        Label l = new Label("Foo");
        Property ds = new MethodProperty<Integer>(Person.createTestPerson1(),
                "age");
        l.setPropertyDataSource(ds);
        assertEquals(String.valueOf(Person.createTestPerson1().getAge()),
                l.getValue());
    }

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
