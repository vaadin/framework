package com.vaadin.tests.server.component.abstractsplitpanel;

import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.AbstractSplitPanel.SplitterClickEvent;
import com.vaadin.ui.AbstractSplitPanel.SplitterClickListener;
import com.vaadin.ui.HorizontalSplitPanel;

public class AbstractSplitPanelListenersTest extends
        AbstractListenerMethodsTestBase {
    public void testSplitterClickListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(HorizontalSplitPanel.class,
                SplitterClickEvent.class, SplitterClickListener.class);
    }
}
