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
}
