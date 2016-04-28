package com.vaadin.tests.tooltip;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

public class ButtonTooltips extends AbstractTestUI {

    public static final String shortDescription = "Another";
    public static final String longDescription = "long descidescidescpription";

    @Override
    protected String getTestDescription() {
        return "Button tooltip's size gets messed up if moving from one tooltip to another before a timer expires.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8454;
    }

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();
        Button button = new Button("One");
        button.setDescription(longDescription);
        Button button2 = new Button("Two");
        button2.setDescription(shortDescription);
        vl.addComponent(button);
        vl.addComponent(button2);
        vl.setComponentAlignment(button, Alignment.TOP_RIGHT);
        vl.setComponentAlignment(button2, Alignment.TOP_RIGHT);
        addComponent(vl);

    }
}
