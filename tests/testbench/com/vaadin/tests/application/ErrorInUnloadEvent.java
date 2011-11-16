package com.vaadin.tests.application;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ErrorInUnloadEvent extends AbstractTestCase {

    private Root mainWindow;

    @Override
    public void init() {
        if (getUser() == null) {
            showLoginWindow();
        } else {
            showMainWindow();
        }
    }

    private void showLoginWindow() {
        if (mainWindow == null) {
            mainWindow = new Root();
        } else {
            mainWindow.removeAllComponents();
        }
        mainWindow.setCaption("Please login");

        FormLayout formLayout = new FormLayout();
        final TextField userField = new TextField("Username");
        userField.setDebugId("user");
        final TextField passwordField = new TextField("Password");
        passwordField.setDebugId("pwd");
        passwordField.setSecret(true);
        Button login = new Button("login");
        login.setDebugId("loginButton");
        login.setClickShortcut(KeyCode.ENTER);
        formLayout.addComponent(userField);
        formLayout.addComponent(passwordField);
        formLayout.addComponent(login);
        mainWindow.setContent(formLayout);

        login.addListener(new ClickListener() {
            public void buttonClick(final ClickEvent event) {
                String username = (String) userField.getValue();
                String password = (String) passwordField.getValue();

                setUser(username);
                showMainWindow();
            }
        });

        setMainWindow(mainWindow);
    }

    private void showMainWindow() {
        if (mainWindow == null) {
            mainWindow = new Root();
        } else {
            mainWindow.removeAllComponents();
        }
        VerticalLayout root = new VerticalLayout();
        root.addComponent(createHeader());

        mainWindow.addComponent(root);
    }

    private Component createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("header-background");
        Label title = new Label("...Title...");
        title.addStyleName("header-title");
        header.addComponent(title);
        Button logout = new Button("Logout");
        logout.addListener(new ClickListener() {
            public void buttonClick(final ClickEvent event) {
                setUser(null);
                showLoginWindow();
            }

        });
        header.addComponent(logout);
        return header;
    }

    @Override
    protected String getDescription() {
        return "Enter a text in the password field and press enter. Then reload the page. No error message should be printed about ignoring a variable change.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6316;
    }
}