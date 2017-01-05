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
package com.vaadin.shared.ui.grid.editor;

import java.util.List;

import com.vaadin.shared.communication.ClientRpc;

/**
 * An RPC interface for the grid editor server-to-client communications.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
public interface EditorClientRpc extends ClientRpc {

    /**
     * Tells the client to cancel editing and hide the editor.
     */
    void cancel();

    /**
     * Confirms a pending {@link EditorServerRpc#bind(String) bind request} sent
     * by the client.
     *
     * @param bindSucceeded
     *            {@code true} if and only if the bind action was successful
     */
    void confirmBind(boolean bindSucceeded);

    /**
     * Confirms a pending {@link EditorServerRpc#save() save request} sent by
     * the client.
     *
     * @param saveSucceeded
     *            {@code true} if and only if the save action was successful
     */
    void confirmSave(boolean saveSucceeded);

    /**
     * Sets the displayed error messages for editor.
     *
     * @param errorMessage
     *            the error message to show the user; {@code} null to clear
     * @param errorColumnsIds
     *            a list of column ids that should get error markers; empty list
     *            to clear
     *
     */
    void setErrorMessage(String errorMessage, List<String> errorColumnsIds);
}
