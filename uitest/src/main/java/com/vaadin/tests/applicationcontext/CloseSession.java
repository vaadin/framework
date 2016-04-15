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

import javax.servlet.http.HttpSession;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;

public class CloseSession extends AbstractTestUI {
    private static final String OLD_HASH_PARAM = "oldHash";
    private static final String OLD_SESSION_ID_PARAM = "oldSessionId";

    private final Log log = new Log(6);

    @Override
    protected void setup(VaadinRequest request) {
        System.out.println("UI " + getUIId() + " inited");

        final int sessionHash = getSession().hashCode();
        final String sessionId = request.getWrappedSession().getId();

        log.log("Current session hashcode: " + sessionHash);
        log.log("Current WrappedSession id: " + sessionId);

        // Log previous values to make it easier to see what has changed
        String oldHashValue = request.getParameter(OLD_HASH_PARAM);
        if (oldHashValue != null) {
            log.log("Old session hashcode: " + oldHashValue);
            log.log("Same hash as current? "
                    + oldHashValue.equals(Integer.toString(sessionHash)));
        }

        String oldSessionId = request.getParameter(OLD_SESSION_ID_PARAM);
        if (oldSessionId != null) {
            log.log("Old WrappedSession id: " + oldSessionId);
            log.log("Same WrappedSession id? " + oldSessionId.equals(sessionId));
        }

        // Add parameters to help see what has changed
        final String reopenUrl = getPage().getLocation().getPath() + "?"
                + OLD_HASH_PARAM + "=" + sessionHash + "&"
                + OLD_SESSION_ID_PARAM + "=" + sessionId;

        addComponent(log);
        addComponent(new Button(
                "Close VaadinServiceSession and redirect elsewhere",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        // Assuming Vaadin is deployed to the root context
                        getPage().setLocation("/statictestfiles/static.html");
                        getSession().close();
                    }
                }));
        addComponent(new Button("Close VaadinServiceSession and reopen page",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getPage().setLocation(reopenUrl);
                        getSession().close();
                    }
                }));
        addComponent(new Button("Just close VaadinSession",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getSession().close();
                    }
                }));
        addComponent(new Button("Just close HttpSession",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getSession().getSession().invalidate();
                    }
                }));
        addComponent(new Button("Invalidate HttpSession and reopen page",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        VaadinService.getCurrentRequest().getWrappedSession()
                                .invalidate();
                        getPage().setLocation(reopenUrl);
                    }
                }));
        addComponent(new Button(
                "Invalidate HttpSession and redirect elsewhere",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        VaadinService.getCurrentRequest().getWrappedSession()
                                .invalidate();
                        getPage().setLocation("/statictestfiles/static.html");
                    }
                }));
        addComponent(new Button(
                "Invalidate HttpSession in a background thread",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        final HttpSession session = ((WrappedHttpSession) VaadinService
                                .getCurrentRequest().getWrappedSession())
                                .getHttpSession();
                        Thread t = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.out
                                        .println("Invalidating session from thread "
                                                + session.getId());
                                session.invalidate();
                                System.out
                                        .println("Invalidated session from thread "
                                                + session.getId());

                            }
                        });
                        t.start();
                    }
                }));
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
        log.log("Detach of " + this + " (" + getUIId() + ")");
        boolean correctUI = (UI.getCurrent() == this);
        boolean correctPage = (Page.getCurrent() == getPage());
        boolean correctVaadinSession = (VaadinSession.getCurrent() == getSession());
        boolean correctVaadinService = (VaadinService.getCurrent() == getSession()
                .getService());
        log.log("UI.current correct in detach: " + correctUI);
        log.log("Page.current correct in detach: " + correctPage);
        log.log("VaadinSession.current correct in detach: "
                + correctVaadinSession);
        log.log("VaadinService.current correct in detach: "
                + correctVaadinService);
    }
}
