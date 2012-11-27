package com.vaadin.client.ui.colorpicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.VColorPickerArea;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.colorpicker.ColorPickerServerRpc;

/**
 * A class that defines an implementation for a color picker connector. Connects
 * the server side {@link com.vaadin.ui.ColorPickerArea} with the client side
 * counterpart {@link VColorPickerArea}
 * 
 * @since 7.0.0
 */
@Connect(com.vaadin.ui.ColorPickerArea.class)
public class ColorPickerAreaConnector extends AbstractColorPickerConnector {

    private ColorPickerServerRpc rpc = RpcProxy.create(
            ColorPickerServerRpc.class, this);

    @Override
    protected Widget createWidget() {
        return GWT.create(VColorPickerArea.class);
    }

    @Override
    public VColorPickerArea getWidget() {
        return (VColorPickerArea) super.getWidget();
    }

    @Override
    public void onClick(ClickEvent event) {
        rpc.openPopup(getWidget().isOpen());
    }

    @Override
    protected void setCaption(String caption) {
        if (getState().htmlContentAllowed) {
            getWidget().setHTML(caption);
        } else {
            getWidget().setText(caption);
        }
    }

    @Override
    protected void refreshColor() {
        getWidget().refreshColor();
    }

}
