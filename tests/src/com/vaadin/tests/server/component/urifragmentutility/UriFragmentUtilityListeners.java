package com.vaadin.tests.server.component.urifragmentutility;

import com.vaadin.tests.server.component.ListenerMethods;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;

public class UriFragmentUtilityListeners extends ListenerMethods {
    public void testFragmentChangedListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(UriFragmentUtility.class,
                FragmentChangedEvent.class, FragmentChangedListener.class);
    }
}
