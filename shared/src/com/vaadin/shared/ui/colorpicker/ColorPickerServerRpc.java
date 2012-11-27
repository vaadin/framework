package com.vaadin.shared.ui.colorpicker;

import com.vaadin.shared.communication.ServerRpc;

/**
 * RPC interface for AbstractColorPicker.
 * 
 * @since 7.0.0
 * 
 */
public interface ColorPickerServerRpc extends ServerRpc {

    /**
     * ColorPicker click event.
     * 
     * @param openPopup
     * 
     */
    public void openPopup(boolean openPopup);

}
