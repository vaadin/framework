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
package com.vaadin.shared.ui.combobox;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Client to server RPC interface for ComboBox.
 * 
 * @since
 */
public interface ComboBoxServerRpc extends ServerRpc {
    /**
     * Create a new item in the combo box. This method can only be used when the
     * ComboBox is configured to allow the creation of new items by the user.
     * 
     * @param itemValue
     *            user entered string value for the new item
     */
    public void createNewItem(String itemValue);

    /**
     * Set the current selection.
     * 
     * @param item
     *            the id of a single item or null to deselect the current value
     */
    public void setSelectedItem(String item);

    /**
     * Request the server to send a page of the item list.
     * 
     * @param filter
     *            filter string interpreted according to the current filtering
     *            mode
     * @param page
     *            zero based page number
     */
    public void requestPage(String filter, int page);
}
