package com.vaadin.tests.server.component.tabsheet;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

public class TabSheetListenersTest extends AbstractListenerMethodsTestBase {
    public void testSelectedTabChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(TabSheet.class, SelectedTabChangeEvent.class,
                SelectedTabChangeListener.class);
    }
}
