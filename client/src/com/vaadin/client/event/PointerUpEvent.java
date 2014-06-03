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
 * Represents a native PointerUpEvent.
 * 
 * @since 7.2
 */
public class PointerUpEvent extends PointerEvent<PointerUpHandler> {

    /**
     * Event type for PointerUpEvent. Represents the meta-data associated with
     * this event.
     */
    private static final Type<PointerUpHandler> TYPE = new Type<PointerUpHandler>(
            EventType.PointerUp.getNativeEventName(), new PointerUpEvent());

    /**
     * Gets the event type associated with PointerUpEvent.
     * 
     * @return the handler type
     */
    public static Type<PointerUpHandler> getType() {
        return TYPE;
    }

    /**
     * Protected constructor, use
     * {@link DomEvent#fireNativeEvent(com.google.gwt.dom.client.NativeEvent, com.google.gwt.event.shared.HasHandlers)}
     * to fire pointer down events.
     */
    protected PointerUpEvent() {
    }

    @Override
    public final Type<PointerUpHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PointerUpHandler handler) {
        handler.onPointerUp(this);
    }

}
