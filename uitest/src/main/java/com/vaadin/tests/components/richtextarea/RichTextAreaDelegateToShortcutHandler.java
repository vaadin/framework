package com.vaadin.tests.components.richtextarea;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

public class RichTextAreaDelegateToShortcutHandler extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        final RichTextArea name = new RichTextArea();
        name.setCaption("Type your name here:");

        Button button = new Button("Click Me");
        button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        button.addClickListener(e -> log("ShortcutHandler invoked " + name.getValue()));

        layout.addComponents(name, button);
        addComponent(layout);
    }
}
