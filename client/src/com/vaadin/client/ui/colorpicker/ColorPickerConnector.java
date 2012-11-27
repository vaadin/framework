package com.vaadin.client.ui.colorpicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.VColorPicker;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.colorpicker.ColorPickerServerRpc;

/**
 * A class that defines default implementation for a color picker connector.
 * Connects the server side {@link com.vaadin.ui.ColorPicker} with the client
 * side counterpart {@link VColorPicker}
 * 
 * @since 7.0.0
 */
@Connect(com.vaadin.ui.ColorPicker.class)
public class ColorPickerConnector extends AbstractColorPickerConnector {

    private ColorPickerServerRpc rpc = RpcProxy.create(
            ColorPickerServerRpc.class, this);

    @Override
    protected Widget createWidget() {
        return GWT.create(VColorPicker.class);
    }

    @Override
    public VColorPicker getWidget() {
        return (VColorPicker) super.getWidget();
    }

    @Override
    public void onClick(ClickEvent event) {
        rpc.openPopup(getWidget().isOpen());
    }

    @Override
    protected void setCaption(String caption) {
        if (getState().htmlContentAllowed) {
            getWidget().setHtml(caption);
        } else {
            getWidget().setText(caption);
        }
    }

    @Override
    protected void refreshColor() {
        getWidget().refreshColor();
    }
}
