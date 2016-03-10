package com.vaadin.tests.server.component.abstractfield;

import com.vaadin.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.CheckBox;

public class AbstractFieldListenersTest extends AbstractListenerMethodsTestBase {
    public void testReadOnlyStatusChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(CheckBox.class,
                ReadOnlyStatusChangeEvent.class,
                ReadOnlyStatusChangeListener.class);
    }

    public void testValueChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(CheckBox.class, ValueChangeEvent.class,
                ValueChangeListener.class);
    }
}
