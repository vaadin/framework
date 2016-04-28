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
package com.vaadin.shared.ui.grid.selection;

import java.util.List;

import com.vaadin.shared.communication.ServerRpc;

/**
 * ServerRpc for MultiSelectionModel.
 * 
 * @since 7.6
 * @author Vaadin Ltd
 */
public interface MultiSelectionModelServerRpc extends ServerRpc {

    /**
     * Select a list of rows based on their row keys on the server-side.
     * 
     * @param rowKeys
     *            list of row keys to select
     */
    public void select(List<String> rowKeys);

    /**
     * Deselect a list of rows based on their row keys on the server-side.
     * 
     * @param rowKeys
     *            list of row keys to deselect
     */
    public void deselect(List<String> rowKeys);

    /**
     * Selects all rows on the server-side.
     */
    public void selectAll();

    /**
     * Deselects all rows on the server-side.
     */
    public void deselectAll();
}
