package com.vaadin.shared.ui.colorpicker;

import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC interface for ColorPickerGradient.
 * 
 * @since 7.0.0
 * 
 */
public interface ColorPickerGradientServerRpc extends ServerRpc {

    /**
     * ColorPickerGradient mouseUp event.
     * 
     * @param cursorX
     * @param cursorY
     */
    public void select(int cursorX, int cursorY);

}
