package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.TextField;

/**
 * Simple program that demonstrates "modal windows" that block all access other
 * windows.
 *
 * @author Vaadin Ltd.
 * @since 4.0.1
 * @see com.vaadin.server.VaadinSession
 * @see com.vaadin.ui.Window
 * @see com.vaadin.ui.Label
 */
public class ModalWindow extends com.vaadin.server.LegacyApplication
        implements ClickListener {

    private Window test;
    private Button reopen;

    @Override
    public void init() {

        // Create main window
        final LegacyWindow main = new LegacyWindow("ModalWindow demo");
        setMainWindow(main);
        main.addComponent(new Label("ModalWindow demo"));

        // Main window textfield
        final TextField f = new TextField();
        f.setTabIndex(1);
        main.addComponent(f);

        // Main window button
        final Button b = new Button("Test Button in main window");
        b.addClickListener(this);
        b.setTabIndex(2);
        main.addComponent(b);

        reopen = new Button("Open modal subwindow");
        reopen.addClickListener(this);
        reopen.setTabIndex(3);
        main.addComponent(reopen);

    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == reopen) {
            openSubWindow();
        }
        getMainWindow().addComponent(
                new Label("Button click: " + event.getButton().getCaption()));
    }

    private void openSubWindow() {
        // Modal window
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        test = new Window("Modal window", layout);
        test.setModal(true);
        getMainWindow().addWindow(test);
        layout.addComponent(new Label(
                "You have to close this window before accessing others."));

        // Textfield for modal window
        final TextField f = new TextField();
        f.setTabIndex(4);
        layout.addComponent(f);
        f.focus();

        // Modal window button
        final Button b = new Button("Test Button in modal window");
        b.setTabIndex(5);
        b.addClickListener(this);
        layout.addComponent(b);
    }
}
