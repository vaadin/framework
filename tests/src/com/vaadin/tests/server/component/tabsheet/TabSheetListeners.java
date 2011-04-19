package com.vaadin.tests.server.component.tabsheet;

import com.vaadin.tests.server.component.ListenerMethods;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

public class TabSheetListeners extends ListenerMethods {
    public void testSelectedTabChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TabSheet.class, SelectedTabChangeEvent.class,
                SelectedTabChangeListener.class);
    }
}
