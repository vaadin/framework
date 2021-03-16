/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.shared.ui.grid.selection;

import com.vaadin.shared.communication.ServerRpc;

/**
 * ServerRpc for SingleSelectionModel.
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public interface SingleSelectionModelServerRpc extends ServerRpc {

    /**
     * Selects a row on server-side.
     *
     * @param rowKey
     *            row key of selected row; {@code null} if deselect
     */
    public void select(String rowKey);
}
