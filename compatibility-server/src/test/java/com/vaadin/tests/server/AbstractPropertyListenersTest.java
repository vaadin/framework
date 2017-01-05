package com.vaadin.tests.server;

import org.junit.Test;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.v7.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.AbstractProperty;
import com.vaadin.v7.data.util.ObjectProperty;

public class AbstractPropertyListenersTest
        extends AbstractListenerMethodsTestBase {

    @Test
    public void testValueChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(AbstractProperty.class, ValueChangeEvent.class,
                ValueChangeListener.class, new ObjectProperty<String>(""));
    }

    @Test
    public void testReadOnlyStatusChangeListenerAddGetRemove()
            throws Exception {
        testListenerAddGetRemove(AbstractProperty.class,
                ReadOnlyStatusChangeEvent.class,
                ReadOnlyStatusChangeListener.class,
                new ObjectProperty<String>(""));
    }
}
