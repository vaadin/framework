/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.shared.ui.tabsheet;

import com.vaadin.shared.communication.ClientRpc;

/**
 * Server to client RPC methods for the TabSheet.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public interface TabsheetClientRpc extends ClientRpc {

    /**
     * Forces the client to switch to the tab that is selected by the server.
     *
     * This is required e.g. for reverting tab selection change on the server
     * side (shared state does not change).
     */
    public void revertToSharedStateSelection();
}
