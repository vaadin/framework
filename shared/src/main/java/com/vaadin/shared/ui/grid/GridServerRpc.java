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
package com.vaadin.shared.ui.grid;

import java.util.List;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.GridConstants.Section;

/**
 * Client-to-server RPC interface for the Grid component.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface GridServerRpc extends ServerRpc {

    void sort(String[] columnIds, SortDirection[] directions,
            boolean userOriginated);

    /**
     * Informs the server that an item has been clicked in Grid.
     *
     * @param rowKey
     *            a key identifying the clicked item
     * @param columnInternalId
     *            column internal id identifying the clicked property
     * @param details
     *            mouse event details
     */
    void itemClick(String rowKey, String columnInternalId,
            MouseEventDetails details);

    /**
     * Informs the server that a context click has happened inside of Grid.
     *
     * @since 7.6
     * @param rowIndex
     *            index of clicked row in Grid section
     * @param rowKey
     *            a key identifying the clicked item
     * @param columnInternalId
     *            column internal id identifying the clicked property
     * @param section
     *            grid section (header, footer, body)
     * @param details
     *            mouse event details
     */
    void contextClick(int rowIndex, String rowKey, String columnInternalId,
            Section section, MouseEventDetails details);

    /**
     * Informs the server that the columns of the Grid have been reordered.
     *
     * @since 7.5.0
     * @param newColumnOrder
     *            a list of column internal ids in the new order
     * @param oldColumnOrder
     *            a list of column internal ids in order before the change
     */
    void columnsReordered(List<String> newColumnOrder,
            List<String> oldColumnOrder);

    /**
     * Informs the server that a column's visibility has been changed.
     *
     * @since 8.0
     * @param columnInternalId
     *            the internal id of the column
     * @param hidden
     *            <code>true</code> if hidden, <code>false</code> if unhidden
     */
    void columnVisibilityChanged(String columnInternalId, boolean hidden);

    /**
     * Informs the server that a column has been resized by the user.
     *
     * @since 7.6
     * @param columnInternalId
     *            the internal id of the column
     * @param pixels
     *            the new width of the column in pixels
     */
    void columnResized(String columnInternalId, double pixels);
}
