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
package com.vaadin.shared.ui.grid;

import com.vaadin.shared.communication.ClientRpc;

/**
 * Server-to-client RPC interface for the Grid component.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface GridClientRpc extends ClientRpc {

    /**
     * Command client Grid to scroll to a specific data row and its (optional)
     * details.
     * 
     * @param row
     *            zero-based row index. If the row index is below zero or above
     *            the row count of the client-side data source, a client-side
     *            exception will be triggered. Since this exception has no
     *            handling by default, an out-of-bounds value will cause a
     *            client-side crash.
     * @param destination
     *            desired placement of scrolled-to row. See the documentation
     *            for {@link ScrollDestination} for more information.
     */
    public void scrollToRow(int row, ScrollDestination destination);

    /**
     * Command client Grid to scroll to the first row.
     */
    public void scrollToStart();

    /**
     * Command client Grid to scroll to the last row.
     */
    public void scrollToEnd();

    /**
     * Command client Grid to recalculate column widths.
     */
    public void recalculateColumnWidths();

    /**
     * Informs the Grid that all items have been selected or not selected on the
     * server side.
     * 
     * @since 7.5.3
     * @param allSelected
     *            <code>true</code> to check the select all checkbox,
     *            <code>false</code> to uncheck it.
     */
    public void setSelectAll(boolean allSelected);
}
