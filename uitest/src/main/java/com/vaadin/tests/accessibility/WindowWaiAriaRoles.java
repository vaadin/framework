package com.vaadin.tests.accessibility;

import java.util.Stack;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.window.WindowRole;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

/**
 * UI to test if subwindows get the correct assistive roles.
 *
 * @author Vaadin Ltd
 */
public class WindowWaiAriaRoles extends AbstractReindeerTestUI {
    Stack<Window> windows = new Stack<>();

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        Button closeButton = new Button("Close windows");
        closeButton.addClickListener(event -> {
            while (!windows.isEmpty()) {
                Window window = windows.pop();
                removeWindow(window);
            }
        });

        Button regularButton = new Button("Regular");
        regularButton.addClickListener(event -> {
            Window regularWindow = new Window("Regular window");
            openWindow(regularWindow);
        });

        Button alertButton = new Button("Alert");
        alertButton.addClickListener(event -> {
            Window alertWindow = new Window("Alert window");
            alertWindow.setAssistiveRole(WindowRole.ALERTDIALOG);
            openWindow(alertWindow);
        });
        addComponent(closeButton);
        addComponent(regularButton);
        addComponent(alertButton);
    }

    void openWindow(Window window) {
        windows.push(window);
        window.center();
        addWindow(window);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "The alert window should have the role 'alertdialog' and the regular window should have the role 'dialog'";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 14289;
    }

}
