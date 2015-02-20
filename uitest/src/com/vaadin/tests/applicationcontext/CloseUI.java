/*
 * Copyright 2012 Vaadin Ltd.
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

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;

public class CloseUI extends AbstractTestUIWithLog {
    private static final String OLD_HASH_PARAM = "oldHash";
    private static final String OLD_SESSION_ID_PARAM = "oldSessionId";

    @Override
    protected void setup(VaadinRequest request) {
        System.out.println("UI " + getUIId() + " inited");

        final int sessionHash = getSession().hashCode();
        final String sessionId = request.getWrappedSession().getId();

        log("Current session hashcode: " + sessionHash);
        log("Current WrappedSession id: " + sessionId);

        // Log previous values to make it easier to see what has changed
        String oldHashValue = request.getParameter(OLD_HASH_PARAM);
        if (oldHashValue != null) {
            log("Old session hashcode: " + oldHashValue);
            log("Same hash as current? "
                    + oldHashValue.equals(Integer.toString(sessionHash)));
        }

        String oldSessionId = request.getParameter(OLD_SESSION_ID_PARAM);
        if (oldSessionId != null) {
            log("Old WrappedSession id: " + oldSessionId);
            log("Same WrappedSession id? " + oldSessionId.equals(sessionId));
        }

        addButton("Log 'hello'", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log("Hello");
            }
        });
        addButton("Close UI", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                close();
            }
        });

        addButton("Close UI (background)", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                new UIRunSafelyThread(CloseUI.this) {
                    @Override
                    protected void runSafely() {
                        close();
                    }
                }.start();
            }
        });
        addButton("Close UI and redirect to /statictestfiles/static.html",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getPage().setLocation("/statictestfiles/static.html");
                        close();
                    }
                });
        addButton(
                "Close UI and redirect to /statictestfiles/static.html (background)",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        new UIRunSafelyThread(CloseUI.this) {

                            @Override
                            protected void runSafely() {
                                getPage().setLocation(
                                        "/statictestfiles/static.html");
                                close();
                            }
                        }.start();
                    }
                });

    }

    @Override
    protected String getTestDescription() {
        return "Test for closing the session and redirecting the user";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9859);
    }

    @Override
    public void detach() {
        super.detach();
        log("Detach of " + this + " (" + getUIId() + ")");
        boolean correctUI = (UI.getCurrent() == this);
        boolean correctPage = (Page.getCurrent() == getPage());
        boolean correctVaadinSession = (VaadinSession.getCurrent() == getSession());
        boolean correctVaadinService = (VaadinService.getCurrent() == getSession()
                .getService());
        log("UI.current correct in detach: " + correctUI);
        log("Page.current correct in detach: " + correctPage);
        log("VaadinSession.current correct in detach: " + correctVaadinSession);
        log("VaadinService.current correct in detach: " + correctVaadinService);
    }
}
