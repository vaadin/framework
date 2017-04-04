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
package com.vaadin.shared.ui.treegrid;

import com.vaadin.shared.communication.ClientRpc;

/**
 * RPC to handle focusing in TreeGrid.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@FunctionalInterface
public interface FocusRpc extends ClientRpc {

    /**
     * Focuses a cell
     *
     * @param rowIndex
     *            the row index
     * @param columnIndex
     *            the cell index
     */
    void focusCell(int rowIndex, int columnIndex);
}
