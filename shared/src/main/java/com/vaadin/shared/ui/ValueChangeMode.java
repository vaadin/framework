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

package com.vaadin.shared.ui;

/**
 * Different modes for when and how often field value changes are transmitted
 * from the client to the server.
 * 
 * @since 8.0
 */
public enum ValueChangeMode {

    /**
     * Fires a server-side event when the field loses focus.
     */
    BLUR,

    /**
     * Fires a server-side event every time the client-side value changes. This
     * gives the least latency but may cause unnecessary traffic.
     */
    EAGER,

    /**
     * Fires a server-side event at defined intervals as long as the value
     * changes from one event to the next. For instance, you can use this mode
     * to transmit a snapshot of the contents of a text area every second as
     * long as the user keeps typing.
     */
    TIMEOUT,

    /**
     * On every user event, schedule a server-side event after a defined
     * interval, cancelling the currently-scheduled event if any. This is a good
     * choice if you want to, for instance, wait for a small break in the user's
     * typing before sending the event.
     */
    LAZY
}
