/*
 * Copyright 2000-2022 Vaadin Ltd.
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

import com.vaadin.shared.annotations.Delayed;
import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC interface for calls from client to server.
 *
 * @since 8.2
 */
public interface AbstractDateFieldServerRpc extends ServerRpc {

    /**
     * Updates the typed data string and resolution names and values.
     *
     * @param newDateString
     *            the value of the text field part. It enables analyzing invalid
     *            input on the server. {@code null} if the date was chosen with
     *            popup calendar or contains user-typed string
     * @param resolutions
     *            map of time unit (resolution) name and value, the key is the
     *            resolution name e.g. "HOUR", "MINUTE", the value can be
     *            {@code null}. If the map is empty, that means the
     *            {@code newDateString} is invalid
     */
    void update(String newDateString, Map<String, Integer> resolutions);

    /**
     * Updates the typed data string and resolution names and values with
     * delayed rpc. The rpc will be sent by triggering another non
     * {@link Delayed} annotated rpc.
     *
     * @since 8.9
     *
     * @param newDateString
     *            the value of the text field part. It enables analyzing invalid
     *            input on the server. {@code null} if the date was chosen with
     *            popup calendar or contains user-typed string
     * @param resolutions
     *            map of time unit (resolution) name and value, the key is the
     *            resolution name e.g. "HOUR", "MINUTE", the value can be
     *            {@code null}. If the map is empty, that means the
     *            {@code newDateString} is invalid
     */
    @Delayed(lastOnly = true)
    void updateValueWithDelay(String newDateString,
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
