package com.vaadin.tests.tooltip;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;

public class TooltipAndJavascript extends AbstractReindeerTestUI {

    @JavaScript("tooltipandjavascript.js")
    public static class MyButton extends Button {

    }

    @Override
    protected void setup(VaadinRequest request) {
        MyButton b = new MyButton();
        b.setCaption("Hover for tooltip");
        b.setDescription("Tooltip for the button");
        addComponent(b);
    }

    @Override
    protected String getTestDescription() {
        return "Hover the button for a tooltip. It should be styled correctly";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14028;
    }

}
