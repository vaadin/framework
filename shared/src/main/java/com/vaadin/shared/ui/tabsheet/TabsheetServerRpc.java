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
package com.vaadin.shared.ui.tabsheet;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Client to server RPC methods for the TabSheet.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public interface TabsheetServerRpc extends ServerRpc {

    /**
     * Tell server that a tab has been selected by the user.
     * 
     * @param key
     *            internal key of the tab
     */
    void setSelected(String key);

    /**
     * Tell server that a tab has been closed by the user.
     * 
     * @param key
     *            internal key of the tab
     */
    void closeTab(String key);

}
