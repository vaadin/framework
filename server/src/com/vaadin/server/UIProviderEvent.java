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

import java.io.Serializable;
import java.util.EventObject;

/**
 * Base class for the events that are sent to various methods in UIProvider.
 * 
 * @see UIProvider
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class UIProviderEvent extends EventObject implements Serializable {

    private final VaadinRequest request;

    /**
     * Creates a new UI provider event.
     * 
     * @param request
     *            the request for which the event is UI provider is invoked
     */
    public UIProviderEvent(VaadinRequest request) {
        super(request.getService());
        this.request = request;
    }

    /**
     * Gets the Vaadin service from which the event originates.
     * 
     * @return the Vaadin service
     */
    public VaadinService getService() {
        return (VaadinService) getSource();
    }

    /**
     * Gets the request associated with this event.
     * 
     * @return the Vaadin request
     */
    public VaadinRequest getRequest() {
        return request;
    }

}
