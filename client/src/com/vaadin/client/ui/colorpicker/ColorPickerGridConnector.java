package com.vaadin.client.ui.colorpicker;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.colorpicker.ColorPickerGridServerRpc;
import com.vaadin.shared.ui.colorpicker.ColorPickerGridState;

/**
 * A class that defines the default implementation for a color picker grid
 * connector. Connects the server side
 * {@link com.vaadin.ui.components.colorpicker.ColorPickerGrid} with the client
 * side counterpart {@link VColorPickerGrid}
 * 
 * @since 7.0.0
 */
@Connect(com.vaadin.ui.components.colorpicker.ColorPickerGrid.class)
public class ColorPickerGridConnector extends AbstractComponentConnector
        implements ClickHandler {

    private ColorPickerGridServerRpc rpc = RpcProxy.create(
            ColorPickerGridServerRpc.class, this);

    @Override
    protected Widget createWidget() {
        return GWT.create(VColorPickerGrid.class);
    }

    @Override
    public VColorPickerGrid getWidget() {
        return (VColorPickerGrid) super.getWidget();
    }

    @Override
    public ColorPickerGridState getState() {
        return (ColorPickerGridState) super.getState();
    }

    @Override
    public void onClick(ClickEvent event) {
        rpc.select(getWidget().getSelectedX(), getWidget().getSelectedY());
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        Set<String> changedProperties = stateChangeEvent.getChangedProperties();
        if (changedProperties.contains("rowCount")
                || changedProperties.contains("columnCount")
                || changedProperties.contains("updateGrid")) {

            getWidget().updateGrid(getState().rowCount, getState().columnCount);
        }
        if (changedProperties.contains("changedX")
                || changedProperties.contains("changedY")
                || changedProperties.contains("changedColor")
                || changedProperties.contains("updateColor")) {

            getWidget().updateColor(getState().changedColor,
                    getState().changedX, getState().changedY);

            if (!getWidget().isGridLoaded()) {
                rpc.refresh();
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        getWidget().addClickHandler(this);
    }
}
