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

import java.util.EventObject;

/**
 * Event gets fired when a new Vaadin session is initialized for a Vaadin
 * service.
 * <p>
 * Because of the way different service instances share the same session, the
 * event is not necessarily fired immediately when the session is created but
 * only when the first request for that session is handled by a specific
 * service.
 * 
 * @see VaadinSessionInitializationListener#vaadinSessionInitialized(VaadinSessionInitializeEvent)
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class VaadinSessionInitializeEvent extends EventObject {

    private final VaadinSession session;
    private final WrappedRequest request;

    /**
     * Creates a new event.
     * 
     * @param service
     *            the Vaadin service from which the event originates
     * @param session
     *            the Vaadin session that has been initialized
     * @param request
     *            the request that triggered the initialization
     */
    public VaadinSessionInitializeEvent(VaadinService service,
            VaadinSession session, WrappedRequest request) {
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
    public VaadinService getVaadinService() {
        return getSource();
    }

    /**
     * Gets the Vaadin session that has been initialized.
     * 
     * @return the Vaadin session
     */
    public VaadinSession getVaadinSession() {
        return session;
    }

    /**
     * Gets the request that triggered the initialization.
     * 
     * @return the request
     */
    public WrappedRequest getRequest() {
        return request;
    }

}
