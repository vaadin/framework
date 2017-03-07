/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.applicationcontext;

import com.vaadin.server.VaadinService;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.LegacyWindow;

public class ChangeSessionId extends AbstractTestCase {

    private Log log = new Log(5);
    Button loginButton = new Button("Change session");
    boolean requestSessionSwitch = false;

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("Sestest Application");
        mainWindow.addComponent(log);
        mainWindow.addComponent(loginButton);
        mainWindow.addComponent(
                new Button("Show session id", new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        logSessionId();
                    }
                }));
        setMainWindow(mainWindow);

        loginButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                String oldSessionId = getSessionId();
                VaadinService
                        .reinitializeSession(VaadinService.getCurrentRequest());
                String newSessionId = getSessionId();
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
        return getContext().getSession().getId();
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
