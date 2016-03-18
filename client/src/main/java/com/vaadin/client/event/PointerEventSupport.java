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

import com.google.gwt.core.client.GWT;
import com.vaadin.client.event.PointerEvent.EventType;

/**
 * Main class for pointer event support. Contains functionality for determining
 * if pointer events are available or not.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class PointerEventSupport {

    private static final PointerEventSupportImpl impl = GWT
            .create(PointerEventSupportImpl.class);

    private PointerEventSupport() {
    }

    public static void init() {
        impl.init();
    }

    /**
     * @return true if pointer events are supported by the browser, false
     *         otherwise
     */
    public static boolean isSupported() {
        return impl.isSupported();
    }

    /**
     * @param eventType
     * @return the native event name of the given event
     */
    public static String getNativeEventName(EventType eventType) {
        return impl.getNativeEventName(eventType);
    }
}
