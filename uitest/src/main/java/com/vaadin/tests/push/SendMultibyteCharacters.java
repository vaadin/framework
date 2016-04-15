package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TextArea;

@Push
public class SendMultibyteCharacters extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TextArea textArea = new TextArea();
        textArea.setImmediate(true);

        addComponent(textArea);
    }

    @Override
    protected Integer getTicketNumber() {
        return 14674;
    }
}
