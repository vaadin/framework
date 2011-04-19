package com.vaadin.tests.server.component.label;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.server.component.ListenerMethods;
import com.vaadin.ui.Label;
import com.vaadin.ui.Label.ValueChangeEvent;

public class LabelListeners extends ListenerMethods {
    public void testValueChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Label.class, ValueChangeEvent.class,
                ValueChangeListener.class);
    }
}
