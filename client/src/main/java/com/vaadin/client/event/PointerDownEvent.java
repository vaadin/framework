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

import com.google.gwt.event.dom.client.DomEvent;

/**
 * Represents a native PointerDownEvent.
 * 
 * @since 7.2
 */
public class PointerDownEvent extends PointerEvent<PointerDownHandler> {

    /**
     * Event type for PointerDownEvent. Represents the meta-data associated with
     * this event.
     */
    private static final Type<PointerDownHandler> TYPE = new Type<PointerDownHandler>(
            EventType.PointerDown.getNativeEventName(), new PointerDownEvent());

    /**
     * Gets the event type associated with PointerDownEvent events.
     * 
     * @return the handler type
     */
    public static Type<PointerDownHandler> getType() {
        return TYPE;
    }

    /**
     * Protected constructor, use
     * {@link DomEvent#fireNativeEvent(com.google.gwt.dom.client.NativeEvent, com.google.gwt.event.shared.HasHandlers)}
     * to fire pointer down events.
     */
    protected PointerDownEvent() {
    }

    @Override
    public final Type<PointerDownHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PointerDownHandler handler) {
        handler.onPointerDown(this);
    }

}
