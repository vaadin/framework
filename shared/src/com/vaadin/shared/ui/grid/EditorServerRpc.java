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

import com.vaadin.shared.communication.ServerRpc;

/**
 * An RPC interface for the grid editor client-to-server communications.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface EditorServerRpc extends ServerRpc {

    /**
     * Asks the server to open the editor and bind data to it. When a bind
     * request is sent, it must be acknowledged with a
     * {@link EditorClientRpc#confirmBind() confirm call} before the client can
     * open the editor.
     * 
     * @param rowIndex
     *            the index of the edited row
     */
    void bind(int rowIndex);

    /**
     * Asks the server to save unsaved changes in the editor to the data source.
     * When a save request is sent, it must be acknowledged with a
     * {@link EditorClientRpc#confirmSave() confirm call}.
     * 
     * @param rowIndex
     *            the index of the edited row
     */
    void save(int rowIndex);

    /**
     * Tells the server to cancel editing. When sending a cancel request, the
     * client does not need to wait for confirmation by the server before hiding
     * the editor.
     * 
     * @param rowIndex
     *            the index of the edited row
     */
    void cancel(int rowIndex);
}
