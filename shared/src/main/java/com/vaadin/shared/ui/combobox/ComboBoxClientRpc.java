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
package com.vaadin.shared.ui.combobox;

import com.vaadin.shared.communication.ClientRpc;

/**
 * Server to client RPC interface for ComboBox.
 *
 * @since 8.3.1
 */
public interface ComboBoxClientRpc extends ClientRpc {

    /**
     * Signal the client that attempt to add a new item failed.
     *
     * @param itemValue
     *            user entered string value for the new item
     */
    public void newItemNotAdded(String itemValue);
}
