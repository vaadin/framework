/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.shared.data.selection;

import java.util.Set;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Transmits SelectionModel selection changes from the client to the server.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 */
public interface MultiSelectServerRpc extends ServerRpc {

    /**
     * Updates the selected items based on their keys.
     *
     * @param addedItemKeys
     *            the item keys added to selection
     * @param removedItemKeys
     *            the item keys removed from selection
     */
    void updateSelection(Set<String> addedItemKeys,
            Set<String> removedItemKeys);
}
