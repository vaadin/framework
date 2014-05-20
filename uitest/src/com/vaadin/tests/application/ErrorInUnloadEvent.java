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
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ErrorInUnloadEvent extends AbstractTestCase {

    private LegacyWindow mainWindow;
    private Object user = null;

    @Override
    public void init() {
        if (user == null) {
            showLoginWindow();
        } else {
            showMainWindow();
        }
    }

    private void showLoginWindow() {
        if (mainWindow == null) {
            mainWindow = new LegacyWindow();
            setMainWindow(mainWindow);
        } else {
            mainWindow.removeAllComponents();
        }
        mainWindow.setCaption("Please login");

        FormLayout formLayout = new FormLayout();
        final TextField userField = new TextField("Username");
        userField.setId("user");
        final PasswordField passwordField = new PasswordField("Password");
        passwordField.setId("pwd");
        Button login = new Button("login");
        login.setId("loginButton");
        login.setClickShortcut(KeyCode.ENTER);
        formLayout.addComponent(userField);
        formLayout.addComponent(passwordField);
        formLayout.addComponent(login);
        mainWindow.setContent(formLayout);

        login.addListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                String username = userField.getValue();
                String password = passwordField.getValue();

                user = username;
                showMainWindow();
            }
        });
    }

    private void showMainWindow() {
        if (mainWindow == null) {
            mainWindow = new LegacyWindow();
            setMainWindow(mainWindow);
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
            @Override
            public void buttonClick(final ClickEvent event) {
                user = null;
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
