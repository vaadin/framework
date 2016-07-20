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
package com.vaadin.shared.tokka.data.selection;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Simple API for SelectionModel client to server communication
 */
public interface SelectionServerRpc extends ServerRpc {

    /**
     * Select an item based on it's key.
     * 
     * @param key
     *            key of item
     */
    void select(String key);

    /**
     * Deselect an item based on it's key.
     * 
     * @param key
     *            key of item
     */
    void deselect(String key);
}
