/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.shared.ui.button;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC interface for calls from client to server.
 *
 * @since 7.0
 */
public interface ButtonServerRpc extends ServerRpc {
    /**
     * Button click event.
     *
     * @param mouseEventDetails
     *            serialized mouse event details
     */
    public void click(MouseEventDetails mouseEventDetails);

    /**
     * Indicate to the server that the client has disabled the button as a
     * result of a click.
     */
    public void disableOnClick();
}
