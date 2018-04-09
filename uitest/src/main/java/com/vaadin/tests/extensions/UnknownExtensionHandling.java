package com.vaadin.tests.extensions;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

public class UnknownExtensionHandling extends AbstractTestUI {

    // Extension without @Connect counterpart
    public static class MyExtension extends AbstractExtension {
        @Override
        public void extend(AbstractClientConnector target) {
            super.extend(target);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label(
                "A label with a missing extension, should cause sensible output in the debug window / browser console");

        MyExtension extension = new MyExtension();
        extension.extend(label);

        addComponent(label);
    }

}
