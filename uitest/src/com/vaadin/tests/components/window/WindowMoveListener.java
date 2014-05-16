package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class WindowMoveListener extends AbstractTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {

        Window w = new Window("Caption");
        w.setId("testwindow");
        w.setHeight("100px");
        w.setWidth("100px");
        w.setPositionX(100);
        w.setPositionY(100);
        addWindow(w);

        Button b = new Button();
        b.setId("testbutton");
        addComponent(b);
        b.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                for (Window window : getWindows()) {
                    window.setPositionX(100);
                    window.setPositionY(100);
                }
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Tests that windows send their updated position "
                + "to server-side after being moved by user";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12885;
    }

}
