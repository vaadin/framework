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

import java.util.EventObject;

/**
 * Event gets fired when a new Vaadin service session is initialized for a
 * Vaadin service.
 * <p>
 * Because of the way different service instances share the same session, the
 * event is not necessarily fired immediately when the session is created but
 * only when the first request for that session is handled by a specific
 * service.
 * 
 * @see SessionInitListener#sessionInit(SessionInitEvent)
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class SessionInitEvent extends EventObject {

    private final VaadinSession session;
    private final VaadinRequest request;

    /**
     * Creates a new event.
     * 
     * @param service
     *            the Vaadin service from which the event originates
     * @param session
     *            the Vaadin service session that has been initialized
     * @param request
     *            the request that triggered the initialization
     */
    public SessionInitEvent(VaadinService service, VaadinSession session,
            VaadinRequest request) {
        super(service);
        this.session = session;
        this.request = request;
    }

    @Override
    public VaadinService getSource() {
        return (VaadinService) super.getSource();
    }

    /**
     * Gets the Vaadin service from which this event originates
     * 
     * @return the Vaadin service instance
     */
    public VaadinService getService() {
        return getSource();
    }

    /**
     * Gets the Vaadin service session that has been initialized.
     * 
     * @return the Vaadin service session
     */
    public VaadinSession getSession() {
        return session;
    }

    /**
     * Gets the request that triggered the initialization.
     * 
     * @return the request
     */
    public VaadinRequest getRequest() {
        return request;
    }

}
