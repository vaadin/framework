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
import com.vaadin.shared.ui.LayoutClickRpc;

public abstract class LayoutClickEventHandler extends AbstractClickEventHandler {

    public LayoutClickEventHandler(ComponentConnector connector) {
        this(connector, EventId.LAYOUT_CLICK_EVENT_IDENTIFIER);
    }

    public LayoutClickEventHandler(ComponentConnector connector,
            String clickEventIdentifier) {
        super(connector, clickEventIdentifier);
    }

    protected abstract ComponentConnector getChildComponent(
            com.google.gwt.user.client.Element element);

    protected ComponentConnector getChildComponent(NativeEvent event) {
        return getChildComponent((com.google.gwt.user.client.Element) event
                .getEventTarget().cast());
    }

    @Override
    protected void fireClick(NativeEvent event) {
        MouseEventDetails mouseDetails = MouseEventDetailsBuilder
                .buildMouseEventDetails(event, getRelativeToElement());
        getLayoutClickRPC().layoutClick(mouseDetails, getChildComponent(event));
    }

    protected abstract LayoutClickRpc getLayoutClickRPC();
}
