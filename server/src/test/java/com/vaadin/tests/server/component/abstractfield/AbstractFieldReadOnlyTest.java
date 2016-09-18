package com.vaadin.tests.server.component.abstractfield;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class AbstractFieldReadOnlyTest {

    Person paulaBean = new Person("Paula", "Brilliant", "paula@brilliant.com",
            34, Sex.FEMALE,
            new Address("Paula street 1", 12345, "P-town", Country.FINLAND));

    @Test
    public void testReadOnlyProperty() {
        TextField tf = new TextField();
        tf.setPropertyDataSource(
                new MethodProperty<String>(paulaBean, "firstName"));
        assertFalse(tf.isReadOnly());
        tf.getPropertyDataSource().setReadOnly(true);
        assertTrue(tf.isReadOnly());
    }

    @Test
    public void testReadOnlyEventFromProperty() {
        final Label valueStore = new Label("");
        TextField tf = new TextField();
        tf.addReadOnlyStatusChangeListener(new ReadOnlyStatusChangeListener() {
            @Override
            public void readOnlyStatusChange(ReadOnlyStatusChangeEvent event) {
                valueStore.setValue("event received!");
            }
        });
        tf.setPropertyDataSource(
                new MethodProperty<String>(paulaBean, "firstName"));
        assertTrue(valueStore.getValue().isEmpty());
        tf.getPropertyDataSource().setReadOnly(true);
        assertFalse(valueStore.getValue().isEmpty());
    }

}
