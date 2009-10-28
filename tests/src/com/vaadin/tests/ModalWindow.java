/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * Simple program that demonstrates "modal windows" that block all access other
 * windows.
 * 
 * @author IT Mill Ltd.
 * @since 4.0.1
 * @see com.vaadin.Application
 * @see com.vaadin.ui.Window
 * @see com.vaadin.ui.Label
 */
public class ModalWindow extends com.vaadin.Application implements
        ClickListener {

    private Window test;
    private Button reopen;

    @Override
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
