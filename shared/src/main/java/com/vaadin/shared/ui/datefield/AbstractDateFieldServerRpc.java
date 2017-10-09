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
     * 
     * @param newDateString
     *            Enables analyzing invalid input on the server this variable is
     *            {@code null} if the date was chosen with popup calendar or
     *            contains user-typed string
     * @param invalidDateString
     *            Whether the last entered date string is invalid or not
     * @param resolutions
     *            map of resolution name and value
     */
    void update(String newDateString, boolean invalidDateString,
            Map<String, Integer> resolutions);

    void blur();

    void focus();
}
