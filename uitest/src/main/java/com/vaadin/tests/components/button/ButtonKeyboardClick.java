package com.vaadin.tests.components.button;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

/**
 * Test UI for availability (x,y) coordinates for button activated via keyboard.
 *
 * @author Vaadin Ltd
 */
public class ButtonKeyboardClick extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Label[] labels = new Label[4];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new Label();
        }

        Button button = new Button("button", event -> {
            Label label = new Label(String.valueOf(event.getClientX()));
            label.addStyleName("x");
            getLayout().replaceComponent(labels[0], label);
            labels[0] = label;

            label = new Label(String.valueOf(event.getClientY()));
            label.addStyleName("y");
            getLayout().replaceComponent(labels[1], label);
            labels[1] = label;

            label = new Label(String.valueOf(event.getRelativeX()));
            label.addStyleName("xRelative");
            getLayout().replaceComponent(labels[2], label);
            labels[2] = label;

            label = new Label(String.valueOf(event.getRelativeY()));
            label.addStyleName("yRelative");
            getLayout().replaceComponent(labels[3], label);
            labels[3] = label;
        });
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Set client coordinates to the middle of the button when click is triggered from keyboard";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12650;
    }

}
