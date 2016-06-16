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
package com.vaadin.tokka.event;

import java.util.EventObject;

import com.vaadin.server.ClientConnector;

/**
 * A base class for user interface events fired by connectors.
 * 
 * @author Vaadin Ltd.
 * @since
 */
public class Event extends EventObject {

    /**
     * {@code true} if event from a client-side update, {@code false} if from an
     * API call.
     */
    private final boolean userOriginated;

    /**
     * Constructs a new event object with source, value and origin.
     * 
     * @param source
     *            source component
     * @param value
     *            pay load value
     * @param userOriginated
     *            is the event from API call or client update
     */
    public Event(ClientConnector source, boolean userOriginated) {
        super(source);
        this.userOriginated = userOriginated;
    }

    /**
     * Gets the origin of this event.
     * 
     * @return {@code true} if update from client-side; {@code false} if from
     *         API call
     */
    public boolean isUserOriginated() {
        return userOriginated;
    }

    @Override
    public ClientConnector getSource() {
        return (ClientConnector) super.getSource();
    }
}
