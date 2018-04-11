package com.vaadin.tests.layouts;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class HorizontalLayoutAndCaretPosition extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        final HorizontalLayout root = new HorizontalLayout();
        root.setSizeFull();
        addComponent(root);
        root.addComponent(new TextField());

        Label l = new Label();
        root.addComponent(l);
        root.setExpandRatio(l, 1);
        root.addComponent(new TextField());
    }

    @Override
    protected String getTestDescription() {
        return "Use IE. Enter some text to the text field. Clicking on the text should position the caret where you clicked, not cause it to jump to the start or the end";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11152;
    }

}
