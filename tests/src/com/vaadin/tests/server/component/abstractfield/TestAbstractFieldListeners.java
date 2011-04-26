package com.vaadin.tests.server.component.abstractfield;

import com.vaadin.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.server.component.ListenerMethods;
import com.vaadin.ui.Button;

public class TestAbstractFieldListeners extends ListenerMethods {
    public void testReadOnlyStatusChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Button.class, ReadOnlyStatusChangeEvent.class,
                ReadOnlyStatusChangeListener.class);
    }

    public void testValueChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Button.class, ValueChangeEvent.class,
                ValueChangeListener.class);
    }
}
