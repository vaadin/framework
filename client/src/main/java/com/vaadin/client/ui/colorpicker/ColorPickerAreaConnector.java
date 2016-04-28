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
import com.vaadin.client.VCaption;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.VColorPickerArea;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.colorpicker.ColorPickerServerRpc;
import com.vaadin.ui.ColorPickerArea;

/**
 * A class that defines an implementation for a color picker connector. Connects
 * the server side {@link com.vaadin.ui.ColorPickerArea} with the client side
 * counterpart {@link VColorPickerArea}
 * 
 * @since 7.0.0
 */
@Connect(value = ColorPickerArea.class, loadStyle = LoadStyle.LAZY)
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
        VCaption.setCaptionText(getWidget(), getState());
    }

    @Override
    protected void refreshColor() {
        getWidget().refreshColor();
    }

}
