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
package com.vaadin.client.event;

import com.vaadin.client.event.PointerEvent.EventType;

/**
 * Main pointer event support implementation class. Made for browser without
 * pointer event support.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class PointerEventSupportImpl {

    /**
     * @return true if the pointer events are supported, false otherwise
     */
    protected boolean isSupported() {
        return false;
    }

    /**
     * @param events
     * @return the native event name of the given event
     */
    public String getNativeEventName(EventType eventName) {
        return eventName.toString().toLowerCase();
    }

    /**
     * Initializes event support
     */
    protected void init() {

    }

}
