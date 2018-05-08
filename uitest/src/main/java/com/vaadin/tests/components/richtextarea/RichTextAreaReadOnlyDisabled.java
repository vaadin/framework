package com.vaadin.tests.components.richtextarea;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.RichTextArea;

public class RichTextAreaReadOnlyDisabled extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        RichTextArea readOnlyDisabledTextArea = new RichTextArea(
                "Readonly & Disabled");
        readOnlyDisabledTextArea.setReadOnly(true);
        readOnlyDisabledTextArea.setEnabled(false);
        readOnlyDisabledTextArea.setValue("Random value");
        readOnlyDisabledTextArea.setId("rtA");

        final Button but1 = new Button("set Read ",
                event -> readOnlyDisabledTextArea
                        .setReadOnly(!readOnlyDisabledTextArea.isReadOnly()));
        but1.setId("readPr");
        final Button but2 = new Button("Enable/Disable ",
                event -> readOnlyDisabledTextArea
                        .setEnabled(!readOnlyDisabledTextArea.isEnabled()));
        but2.setId("enablePr");
        addComponent(readOnlyDisabledTextArea);
        addComponent(but1);
        addComponent(but2);
    }
}
