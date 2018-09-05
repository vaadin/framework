/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.shared.ui.ui;

import com.vaadin.shared.communication.ClientRpc;

/**
 * Server to Client RPC methods for UI.
 *
 * @since 7.1
 * @author Vaadin Ltd
 */
public interface UIClientRpc extends ClientRpc {

    /**
     * Informs the client that the UI has been closed.
     *
     * @param sessionExpired
     *            true if the ui was closed because the session expired, false
     *            otherwise
     */
    void uiClosed(boolean sessionExpired);

}
