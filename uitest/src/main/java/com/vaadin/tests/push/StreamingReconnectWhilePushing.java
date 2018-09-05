package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

@Push(transport = Transport.STREAMING)
public class StreamingReconnectWhilePushing extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            private Label label;

            @Override
            public void buttonClick(ClickEvent event) {
                if (label == null) {
                    label = new Label();
                    label.setValue(getString(1000000));
                    addComponent(label);
                } else {
                    label.setValue("." + label.getValue());
                }

            }
        });
        addComponent(button);

    }

    protected String getString(int len) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i % 100 == 0) {
                b.append("\n");
            } else {
                b.append('A');
            }

        }
        return b.toString();
    }

    @Override
    protected String getTestDescription() {
        return "Each push of the button sends about 1MB to the client. Press it a couple of times and a spinner will appear forever if reconnecting does not work.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13435;
    }

}
