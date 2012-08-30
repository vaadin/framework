package com.vaadin.tests.components.button;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class DisabledButtons extends TestBase {

    private static final ThemeResource ICON = new ThemeResource(
            "../runo/icons/16/ok.png");
    private String CAPTION = "Caption";

    @Override
    protected String getDescription() {
        return "The disabled buttons should be identical to the enabled buttons but grayed out.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3110;
    }

    @Override
    protected void setup() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(createButtons(true));
        hl.addComponent(createButtons(false));

        addComponent(hl);

    }

    private Component createButtons(boolean enabled) {
        VerticalLayout vl = new VerticalLayout();
        Button b;

        // Button w/ text
        b = new Button(CAPTION);
        b.setEnabled(enabled);
        vl.addComponent(b);

        // Button w/ text, icon
        b = new Button(CAPTION);
        b.setEnabled(enabled);
        b.setIcon(ICON);
        vl.addComponent(b);

        // Button w/ icon
        b = new Button();
        b.setEnabled(enabled);
        b.setIcon(ICON);
        vl.addComponent(b);

        return vl;
    }

}
