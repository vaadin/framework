/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.server;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * Web application context for Vaadin applications.
 * 
 * This is automatically added as a {@link HttpSessionBindingListener} when
 * added to a {@link HttpSession}.
 * 
 * @author Vaadin Ltd.
 * @since 3.1
 * 
 * @deprecated might be refactored or removed before 7.0.0
 */
@Deprecated
@SuppressWarnings("serial")
public class VaadinServletSession extends VaadinServiceSession {

    private transient boolean reinitializingSession = false;

    /**
     * Create a servlet service session for the given servlet service
     * 
     * @param service
     *            the servlet service to which the new session belongs
     */
    public VaadinServletSession(VaadinServletService service) {
        super(service);
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        if (!reinitializingSession) {
            // Avoid closing the application if we are only reinitializing the
            // session. Closing the application would cause the state to be lost
            // and a new application to be created, which is not what we want.
            super.valueUnbound(event);
        }
    }

    /**
     * Discards the current session and creates a new session with the same
     * contents. The purpose of this is to introduce a new session key in order
     * to avoid session fixation attacks.
     */
    public void reinitializeSession() {
        HttpServletRequest currentRequest = VaadinServletService
                .getCurrentServletRequest();
        if (currentRequest == null) {
            throw new IllegalStateException(
                    "Can not reinitialize session outside normal request handling.");
        }

        HttpSession oldSession = getHttpSession();

        // Stores all attributes (security key, reference to this context
        // instance) so they can be added to the new session
        HashMap<String, Object> attrs = new HashMap<String, Object>();
        for (Enumeration<String> e = oldSession.getAttributeNames(); e
                .hasMoreElements();) {
            String name = e.nextElement();
            attrs.put(name, oldSession.getAttribute(name));
        }

        // Invalidate the current session, set flag to avoid call to
        // valueUnbound
        reinitializingSession = true;
        oldSession.invalidate();
        reinitializingSession = false;

        // Create a new session
        HttpSession newSession = currentRequest.getSession();

        // Restores all attributes (security key, reference to this context
        // instance)
        for (String name : attrs.keySet()) {
            newSession.setAttribute(name, attrs.get(name));
        }

        // Update the "current session" variable
        storeInSession(VaadinService.getCurrent(), new WrappedHttpSession(
                newSession));
    }

    /**
     * Gets the http-session application is running in.
     * 
     * @return HttpSession this application context resides in.
     */
    public HttpSession getHttpSession() {
        WrappedSession session = getSession();
        return ((WrappedHttpSession) session).getHttpSession();
    }

}
