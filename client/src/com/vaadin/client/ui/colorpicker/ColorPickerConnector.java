/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.client.ui.colorpicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.VColorPicker;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.colorpicker.ColorPickerServerRpc;
import com.vaadin.ui.ColorPicker;

/**
 * A class that defines default implementation for a color picker connector.
 * Connects the server side {@link com.vaadin.ui.ColorPicker} with the client
 * side counterpart {@link VColorPicker}
 * 
 * @since 7.0.0
 */
@Connect(value = ColorPicker.class, loadStyle = LoadStyle.LAZY)
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
