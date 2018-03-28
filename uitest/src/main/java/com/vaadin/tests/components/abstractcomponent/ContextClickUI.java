package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;

public class ContextClickUI extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final ContextClickListener listener = event -> log(
                "Received context click at (" + event.getClientX() + ", "
                        + event.getClientY() + ")");
        getUI().addContextClickListener(listener);

        addComponent(new Button("Remove listener",
                event -> getUI().removeContextClickListener(listener)));
    }
}
