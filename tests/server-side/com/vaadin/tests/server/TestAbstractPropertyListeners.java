package com.vaadin.tests.server;

import com.vaadin.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.tests.server.component.AbstractListenerMethodsTest;

public class TestAbstractPropertyListeners extends AbstractListenerMethodsTest {
    public void testValueChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(AbstractProperty.class,
                ValueChangeEvent.class, ValueChangeListener.class,
                new ObjectProperty<String>(""));
    }

    public void testReadOnlyStatusChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(AbstractProperty.class,
                ReadOnlyStatusChangeEvent.class,
                ReadOnlyStatusChangeListener.class, new ObjectProperty<String>(
                        ""));
    }
}
