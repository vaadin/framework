package com.vaadin.tests.components.nativebutton;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;

/**
 * UI used to validate click coordinates reported from clicks on NativeButton
 * elements.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class NativeButtonClick extends AbstractReindeerTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final Label label1 = new Label("0,0");
        final Label label2 = new Label("0,0");

        Button button1 = new NativeButton("Button1", event -> label1
                .setValue(event.getClientX() + "," + event.getClientY()));
        Button button2 = new NativeButton("Button2", event -> label2
                .setValue(event.getClientX() + "," + event.getClientY()));

        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponents(button1, button2, label1, label2);
        layout.setSpacing(true);
        addComponent(layout);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Validate click event coordinates not erroneously returned as x=0, y=0";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 14022;
    }

}
