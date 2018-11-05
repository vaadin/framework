package com.vaadin.tests.components.richtextarea;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

public class RichTextAreaRelativeHeightResize extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setHeight("300px");

        RichTextArea richTextArea = new RichTextArea();
        richTextArea.setSizeFull();
        layout.addComponent(richTextArea);

        addComponent(layout);
        addComponent(new Button("Increase height",
                event -> layout.setHeight("400px")));

    }

    @Override
    protected String getTestDescription() {
        return "Tests that a RichTextArea with dynamic height "
                + "updates its editor elements height on resize";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11320;
    }

}
