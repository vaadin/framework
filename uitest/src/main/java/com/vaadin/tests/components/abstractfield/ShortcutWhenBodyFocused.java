package com.vaadin.tests.components.abstractfield;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;

public class ShortcutWhenBodyFocused extends AbstractTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        Button b = new Button("Hello", event -> log("Hello clicked"));
        b.setClickShortcut(KeyCode.A);
        addComponent(b);

        getPage().getStyles().add("body { width: 50% !important}");
    }

}
