/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.v7.client.ui.colorpicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.colorpicker.VColorPickerGradient;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.v7.client.ui.AbstractLegacyComponentConnector;
import com.vaadin.v7.shared.ui.colorpicker.ColorPickerGradientServerRpc;
import com.vaadin.v7.shared.ui.colorpicker.ColorPickerGradientState;
import com.vaadin.v7.ui.components.colorpicker.ColorPickerGradient;

/**
 * A class that defines the default implementation for a color picker gradient
 * connector. Connects the server side {@link ColorPickerGradient} with the
 * client side counterpart {@link VColorPickerGradient}
 *
 * @since 7.0.0
 */
@Connect(value = ColorPickerGradient.class, loadStyle = LoadStyle.LAZY)
public class ColorPickerGradientConnector
        extends AbstractLegacyComponentConnector implements MouseUpHandler {

    private ColorPickerGradientServerRpc rpc = RpcProxy
            .create(ColorPickerGradientServerRpc.class, this);

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
        if (stateChangeEvent.hasPropertyChanged("cursorX")
                || stateChangeEvent.hasPropertyChanged("cursorY")) {

            getWidget().setCursor(getState().cursorX, getState().cursorY);
        }
        if (stateChangeEvent.hasPropertyChanged("bgColor")) {
            getWidget().setBGColor(getState().bgColor);
        }
    }

    @Override
    protected void init() {
        super.init();
        getWidget().addMouseUpHandler(this);
    }

}
