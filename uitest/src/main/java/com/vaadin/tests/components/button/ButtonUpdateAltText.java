package com.vaadin.tests.components.button;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;

/**
 *
 * @author Vaadin Ltd
 */
public class ButtonUpdateAltText extends AbstractReindeerTestUI {

    private final ThemeResource ICON = new ThemeResource(
            "../runo/icons/16/folder.png");

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final Button btn = new Button();
        btn.setId("button");
        btn.setIcon(ICON, "initial alt text");
        addComponent(btn);

        final CheckBox enable = new CheckBox("Enable alt text", true);
        enable.addValueChangeListener(event -> {
            if (event.getValue()) {
                btn.setIconAlternateText("alt text");
            } else {
                btn.setIconAlternateText("");
            }
        });
        addComponent(enable);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Button should have a alt text";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12333;
    }

}
