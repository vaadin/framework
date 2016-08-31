package com.vaadin.tests.widgetset.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ui.VLabel;

public class AssertionFailureWidget extends SimplePanel {

    public AssertionFailureWidget() {
        Scheduler.get().scheduleDeferred(() -> {
            assert 1 == 2 : "This should fail.";
            VLabel w = new VLabel();
            add(w);
            w.setText("This should not be here.");
            w.addStyleName("non-existent-widget");
        });
    }
}
