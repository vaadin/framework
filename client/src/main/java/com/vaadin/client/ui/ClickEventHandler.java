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
package com.vaadin.client.ui;

import com.google.gwt.dom.client.NativeEvent;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;

public abstract class ClickEventHandler extends AbstractClickEventHandler {

    public ClickEventHandler(ComponentConnector connector) {
        this(connector, EventId.CLICK_EVENT_IDENTIFIER);
    }

    public ClickEventHandler(ComponentConnector connector,
            String clickEventIdentifier) {
        super(connector, clickEventIdentifier);
    }

    /**
     * Sends the click event based on the given native event. Delegates actual
     * sending to {@link #fireClick(MouseEventDetails)}.
     * 
     * @param event
     *            The native event that caused this click event
     */
    @Override
    protected void fireClick(NativeEvent event) {
        MouseEventDetails mouseDetails = MouseEventDetailsBuilder
                .buildMouseEventDetails(event, getRelativeToElement());
        fireClick(event, mouseDetails);
    }

    /**
     * Sends the click event to the server. Must be implemented by sub classes,
     * typically by calling an RPC method.
     * 
     * @param event
     *            The event that caused this click to be fired
     * 
     * @param mouseDetails
     *            The mouse details for the event
     */
    protected abstract void fireClick(NativeEvent event,
            MouseEventDetails mouseDetails);

}
