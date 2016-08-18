package com.vaadin.tests.server.component.abstractfield;

import org.junit.Test;

import com.vaadin.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.Table;

public class LegacyAbstractFieldListenersTest
        extends AbstractListenerMethodsTestBase {

    @Test
    public void testReadOnlyStatusChangeListenerAddGetRemove()
            throws Exception {
        testListenerAddGetRemove(Table.class, ReadOnlyStatusChangeEvent.class,
                ReadOnlyStatusChangeListener.class);
    }

    @Test
    public void testValueChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Table.class, ValueChangeEvent.class,
                ValueChangeListener.class);
    }
}
