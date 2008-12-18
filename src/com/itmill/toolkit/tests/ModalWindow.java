/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

/**
 * Simple program that demonstrates "modal windows" that block all access other
 * windows.
 * 
 * @author IT Mill Ltd.
 * @since 4.0.1
 * @see com.itmill.toolkit.Application
 * @see com.itmill.toolkit.ui.Window
 * @see com.itmill.toolkit.ui.Label
 */
public class ModalWindow extends com.itmill.toolkit.Application implements
        ClickListener {

    private Window test;
    private Button reopen;

    public void init() {

        // Create main window
        final Window main = new Window("ModalWindow demo");
        setMainWindow(main);
        main.addComponent(new Label("ModalWindow demo"));

        // Main window textfield
        final TextField f = new TextField();
        f.setTabIndex(1);
        main.addComponent(f);

        // Main window button
        final Button b = new Button("Test Button in main window");
        b.addListener(this);
        b.setTabIndex(2);
        main.addComponent(b);

        reopen = new Button("Open modal subwindow");
        reopen.addListener(this);
        reopen.setTabIndex(3);
        main.addComponent(reopen);

    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == reopen) {
            openSubWindow();
        }
        getMainWindow().addComponent(
                new Label("Button click: " + event.getButton().getCaption()));
    }

    private void openSubWindow() {
        // Modal window
        test = new Window("Modal window");
        test.setModal(true);
        getMainWindow().addWindow(test);
        test.addComponent(new Label(
                "You have to close this window before accessing others."));

        // Textfield for modal window
        final TextField f = new TextField();
        f.setTabIndex(4);
        test.addComponent(f);
        f.focus();

        // Modal window button
        final Button b = new Button("Test Button in modal window");
        b.setTabIndex(5);
        b.addListener(this);
        test.addComponent(b);
    }
}
