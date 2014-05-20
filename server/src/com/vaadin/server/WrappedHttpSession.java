/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

/**
 * Wrapper for {@link HttpSession}.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class WrappedHttpSession implements WrappedSession {

    private final HttpSession session;

    /**
     * Creates a new wrapped http session.
     * 
     * @param session
     *            the http session to wrap.
     */
    public WrappedHttpSession(HttpSession session) {
        this.session = session;
    }

    @Override
    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    @Override
    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    /**
     * Gets the wrapped {@link HttpSession}.
     * 
     * @return the wrapped http session
     */
    public HttpSession getHttpSession() {
        return session;
    }

    @Override
    public Set<String> getAttributeNames() {
        Enumeration<String> attributeNames = session.getAttributeNames();
        return enumerationToSet(attributeNames);
    }

    // Helper shared with WrappedPortletSession
    static <T> Set<T> enumerationToSet(Enumeration<T> values) {
        HashSet<T> set = new HashSet<T>();
        while (values.hasMoreElements()) {
            set.add(values.nextElement());
        }
        return Collections.unmodifiableSet(set);
    }

    @Override
    public void invalidate() {
        if (session == null) {
            throw new IllegalStateException(
                    "Session is null and cannot be invalidated");
        }

        if (session.getClass().getName()
                .equals("org.atmosphere.util.FakeHttpSession")) {
            throw new UnsupportedOperationException(
                    "FakeHttpSession cannot be invalidated. "
                            + "This typically means you are using websockets together with Tomcat 7. "
                            + "Because Tomcat 7 does not support sharing the HTTP session between standard HTTP requests and websockets, a copy of the session is used for websockets. "
                            + "Invalidating this session does not have the desired effect. "
                            + "To resolve this, upgrade to Tomcat 8 or use another transport mechanism than websockets.");
        }
        session.invalidate();
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public long getCreationTime() {
        return session.getCreationTime();
    }

    @Override
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    @Override
    public boolean isNew() {
        return session.isNew();
    }

    @Override
    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }

}
