package com.vaadin.tests.components.richtextarea;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;

public class RichTextAreaEmptyString extends TestBase {

    @Override
    protected String getDescription() {
        return "Test the value of a rich text area. Visually empty area should return \"\"";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8004;
    }

    @Override
    protected void setup() {
        final RichTextArea area = new RichTextArea();

        final Label l = new Label(area.getValue(), ContentMode.PREFORMATTED);
        l.setCaption("Value recieved from RichTextArea:");

        final Button b = new Button("get area value",
                event -> l.setValue(area.getValue()));

        addComponent(area);
        addComponent(b);
        addComponent(l);
    }

}
