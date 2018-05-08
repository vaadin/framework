package com.vaadin.tests.widgetset.client.v7.grid;

import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.tests.widgetset.client.v7.grid.GridClientColumnRendererConnector.Renderers;

public interface GridClientColumnRendererRpc extends ClientRpc {

    /**
     * Adds a new column with a specific renderer to the grid
     *
     */
    void addColumn(Renderers renderer);

    /**
     * Detaches and attaches the client side Grid
     */
    void detachAttach();

    /**
     * Used for client-side sorting API test
     */
    void triggerClientSorting();

    void triggerClientSortingTest();

    void shuffle();
}
