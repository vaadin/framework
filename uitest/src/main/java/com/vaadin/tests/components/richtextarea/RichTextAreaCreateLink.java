package com.vaadin.tests.components.richtextarea;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;

public class RichTextAreaCreateLink extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "Test the 'Create Link' button of a rich text area in supported browsers.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11888;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final RichTextArea area = new RichTextArea();

        final Label label = new Label(area.getValue(),
                ContentMode.PREFORMATTED);
        label.setCaption("Value recieved from RichTextArea:");

        final Button button = new Button("get area value",
                event -> label.setValue(area.getValue()));

        addComponent(area);
        addComponent(button);
        addComponent(label);
    }

}
