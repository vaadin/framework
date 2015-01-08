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
package com.vaadin.shared.ui.grid;

import com.vaadin.shared.communication.ClientRpc;

/**
 * An RPC interface for the grid editor server-to-client communications.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface EditorClientRpc extends ClientRpc {

    /**
     * Tells the client to open the editor and bind data to it.
     * 
     * @param rowIndex
     *            the index of the edited row
     */
    void bind(int rowIndex);

    /**
     * Tells the client to cancel editing and hide the editor.
     * 
     * @param rowIndex
     *            the index of the edited row
     */
    void cancel(int rowIndex);

    /**
     * Confirms a pending {@link EditorServerRpc#bind(int) bind request}
     * sent by the client.
     */
    void confirmBind();

    /**
     * Confirms a pending {@link EditorServerRpc#save(int) save request}
     * sent by the client.
     */
    void confirmSave();
}
