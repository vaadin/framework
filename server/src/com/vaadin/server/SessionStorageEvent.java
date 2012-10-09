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

import java.util.EventObject;

/**
 * Information about a session storage operation.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class SessionStorageEvent extends EventObject {

    private final VaadinService service;
    private final VaadinRequest request;
    private final VaadinResponse response;

    /**
     * Creates a new session storage event.
     * 
     * @param service
     *            the Vaadin service that that the session should be associated
     *            with
     * @param request
     *            the Vaadin request for the event
     * @param response
     *            the Vaadin response for the event
     */
    public SessionStorageEvent(VaadinService service, VaadinRequest request,
            VaadinResponse response) {
        super(service);
        this.service = service;
        this.request = request;
        this.response = response;
    }

    /**
     * Gets the Vaadin service for the event.
     * 
     * @return the Vaadin service
     */
    public VaadinService getService() {
        return service;
    }

    /**
     * Gets the Vaadin request for the event.
     * 
     * @return the Vaadin request
     */
    public VaadinRequest getRequest() {
        return request;
    }

    /**
     * Gets the Vaadin response for the event.
     * 
     * @return the Vaadin response
     */
    public VaadinResponse getResponse() {
        return response;
    }

}
