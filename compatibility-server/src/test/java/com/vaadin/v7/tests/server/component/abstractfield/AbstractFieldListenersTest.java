package com.vaadin.v7.tests.server.component.abstractfield;

import org.junit.Test;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.v7.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.CheckBox;

public class AbstractFieldListenersTest
        extends AbstractListenerMethodsTestBase {

    @Test
    public void testReadOnlyStatusChangeListenerAddGetRemove()
            throws Exception {
        testListenerAddGetRemove(CheckBox.class,
                ReadOnlyStatusChangeEvent.class,
                ReadOnlyStatusChangeListener.class);
    }

    @Test
    public void testValueChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(CheckBox.class, ValueChangeEvent.class,
                ValueChangeListener.class);
    }
}
