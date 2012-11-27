package com.vaadin.client.ui.colorpicker;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.colorpicker.ColorPickerGradientServerRpc;
import com.vaadin.shared.ui.colorpicker.ColorPickerGradientState;

/**
 * A class that defines the default implementation for a color picker gradient
 * connector. Connects the server side
 * {@link com.vaadin.ui.components.colorpicker.ColorPickerGradient} with the
 * client side counterpart {@link VColorPickerGradient}
 * 
 * @since 7.0.0
 */
@Connect(com.vaadin.ui.components.colorpicker.ColorPickerGradient.class)
public class ColorPickerGradientConnector extends AbstractComponentConnector
        implements MouseUpHandler {

    private ColorPickerGradientServerRpc rpc = RpcProxy.create(
            ColorPickerGradientServerRpc.class, this);

    @Override
    protected Widget createWidget() {
        return GWT.create(VColorPickerGradient.class);
    }

    @Override
    public VColorPickerGradient getWidget() {
        return (VColorPickerGradient) super.getWidget();
    }

    @Override
    public ColorPickerGradientState getState() {
        return (ColorPickerGradientState) super.getState();
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        rpc.select(getWidget().getCursorX(), getWidget().getCursorY());
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        Set<String> changedProperties = stateChangeEvent.getChangedProperties();
        if (changedProperties.contains("cursorX")
                || changedProperties.contains("cursorY")) {

            getWidget().setCursor(getState().cursorX, getState().cursorY);
        }
        if (changedProperties.contains("bgColor")) {
            getWidget().setBGColor(getState().bgColor);
        }
    }

    @Override
    protected void init() {
        super.init();
        getWidget().addMouseUpHandler(this);
    }

}