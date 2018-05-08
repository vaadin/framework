package com.vaadin.tests.server.component.label;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.VaadinSession;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.util.CurrentInstance;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.MethodProperty;
import com.vaadin.v7.ui.Label;

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
