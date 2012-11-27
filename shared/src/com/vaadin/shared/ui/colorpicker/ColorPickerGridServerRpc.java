package com.vaadin.shared.ui.colorpicker;

import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC interface for ColorPickerGrid.
 * 
 * @since 7.0.0
 * 
 */
public interface ColorPickerGridServerRpc extends ServerRpc {

    /**
     * ColorPickerGrid click event.
     * 
     * @param x
     * @param y
     */
    public void select(int x, int y);

    /**
     * Call to refresh the grid.
     */
    public void refresh();

}
