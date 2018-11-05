package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * Test to demonstrate that tooltips are shown for both Window header and
 * content
 *
 * @author Vaadin Ltd
 */
public class ToolTipInWindow extends AbstractReindeerTestUI {

    Window window;

    @Override
    protected void setup(VaadinRequest request) {

        window = new Window("Caption", new Label("A label content"));
        window.setPositionX(300);
        window.setPositionY(200);
        window.setWidth("200px");
        window.setHeight("200px");
        window.setDescription("Tooltip");
        addWindow(window);

    }

}
