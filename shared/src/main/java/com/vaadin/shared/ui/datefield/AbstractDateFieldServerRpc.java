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
package com.vaadin.shared.ui.datefield;

import java.util.Map;

import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC interface for calls from client to server.
 *
 * @since
 */
public interface AbstractDateFieldServerRpc extends ServerRpc {

    /**
     * Updates the typed data string and resolution names and values.
     * 
     * @param newDateString
     *            the value of the text field part. It enables analyzing invalid
     *            input on the server. {@code null} if the date was chosen with
     *            popup calendar or contains user-typed string
     * @param invalidDateString
     *            Whether the last date string is invalid or not
     * @param resolutions
     *            map of time unit (resolution) name and value, name is the
     *            lower-case resolution name e.g. "hour", "minute", and value
     *            can be {@code null}
     */
    void update(String newDateString, boolean invalidDateString,
            Map<String, Integer> resolutions);

    /**
     * Indicates to the server that the client-side has lost focus.
     */
    void blur();

    /**
     * Indicates to the server that the client-side has acquired focus.
     */
    void focus();
}
