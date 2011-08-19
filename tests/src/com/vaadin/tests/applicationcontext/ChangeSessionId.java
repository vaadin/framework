package com.vaadin.tests.applicationcontext;

import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;

public class ChangeSessionId extends AbstractTestCase {

    private Log log = new Log(5);
    Button loginButton = new Button("Change session");
    boolean requestSessionSwitch = false;

    @Override
    public void init() {
        Window mainWindow = new Window("Sestest Application");
        mainWindow.addComponent(log);
        mainWindow.addComponent(loginButton);
        mainWindow.addComponent(new Button("Show session id",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        logSessionId();
                    }
                }));
        setMainWindow(mainWindow);

        loginButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                WebApplicationContext context = ((WebApplicationContext) getContext());

                String oldSessionId = context.getHttpSession().getId();
                context.reinitializeSession();
                String newSessionId = context.getHttpSession().getId();
                if (oldSessionId.equals(newSessionId)) {
                    log.log("FAILED! Both old and new session id is "
                            + newSessionId);
                } else {
                    log.log("Session id changed successfully from "
                            + oldSessionId + " to " + newSessionId);
                }

            }
        });
        logSessionId();
    }

    private void logSessionId() {
        log.log("Session id: " + getSessionId());
    }

    protected String getSessionId() {
        return ((WebApplicationContext) getContext()).getHttpSession().getId();
    }

    @Override
    protected String getDescription() {
        return "Tests that the session id can be changed to prevent session fixation attacks";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6094;
    }

}