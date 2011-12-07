package com.vaadin.tests.server.component.urifragmentutility;

import com.vaadin.tests.server.component.AbstractListenerMethodsTest;
import com.vaadin.ui.Root;
import com.vaadin.ui.Root.FragmentChangedEvent;
import com.vaadin.ui.Root.FragmentChangedListener;

public class UriFragmentUtilityListeners extends AbstractListenerMethodsTest {
    public void testFragmentChangedListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(Root.class, FragmentChangedEvent.class,
                FragmentChangedListener.class);
    }
}
