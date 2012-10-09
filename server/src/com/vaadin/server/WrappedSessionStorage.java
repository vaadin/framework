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

package com.vaadin.server;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The default Vaadin session storage implementation that stores Vaadin sessions
 * using {@link WrappedSession}.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class WrappedSessionStorage implements VaadinServiceSessionStorage {

    @Override
    public VaadinServiceSession loadSession(SessionStorageEvent event) {
        WrappedSession wrappedSession = event.getRequest().getWrappedSession(
                false);
        if (wrappedSession == null) {
            return null;
        }
        Object attribute = wrappedSession.getAttribute(getAttributeName(event));
        if (attribute instanceof VaadinServiceSession) {
            VaadinServiceSession session = (VaadinServiceSession) attribute;
            return session;
        } else {
            return null;
        }
    }

    private static String getAttributeName(SessionStorageEvent event) {
        VaadinService service = event.getService();
        String name = VaadinServiceSession.class.getName() + "."
                + service.getServiceName();
        return name;
    }

    @Override
    public void storeSession(VaadinServiceSession session,
            SessionStorageEvent event) {
        WrappedSession wrappedSession = event.getRequest().getWrappedSession();
        wrappedSession.setAttribute(getAttributeName(event), session);
    }

    @Override
    public void removeSession(VaadinServiceSession session,
            SessionStorageEvent event) {
        WrappedSession wrappedSession = event.getRequest().getWrappedSession(
                false);
        if (wrappedSession != null) {
            wrappedSession.setAttribute(getAttributeName(event), null);
        }
    }

    @Override
    public Lock getSessionLock(VaadinServiceSession session) {
        String lockAttribute = WrappedSessionStorage.class.getName() + ".lock";
        Object lock = session.getAttribute(lockAttribute);
        if (lock == null) {
            lock = new ReentrantLock();
            session.setAttribute(lockAttribute, lock);
        }
        return (Lock) lock;
    }

    @Override
    public int getSessionStorageTime(SessionStorageEvent event) {
        WrappedSession wrappedSession = event.getRequest().getWrappedSession(
                false);
        if (wrappedSession == null) {
            return -1;
        } else {
            return wrappedSession.getMaxInactiveInterval();
        }
    }

}
