/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.jsinterop;

import com.google.gwt.user.client.Event;

import jsinterop.annotations.JsFunction;

/**
 * JS Interop event listener to be added to {@link JsEventTarget}.
 */
@JsFunction
public interface JsEventListener {

    /**
     * Event callback function.
     *
     * @param event
     *         Browser event.
     */
    public void onEvent(Event event);
}
