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

package com.vaadin.client.ui.nativeselect;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.EventHelper;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.VNativeSelect;
import com.vaadin.client.ui.optiongroup.OptionGroupBaseConnector;
import com.vaadin.shared.communication.FieldRpc.FocusAndBlurServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.NativeSelect;

@Connect(NativeSelect.class)
public class NativeSelectConnector extends OptionGroupBaseConnector implements
        BlurHandler, FocusHandler {

    private HandlerRegistration focusHandlerRegistration = null;
    private HandlerRegistration blurHandlerRegistration = null;

    public NativeSelectConnector() {
        super();
    }

    @OnStateChange("registeredEventListeners")
    private void onServerEventListenerChanged() {
        focusHandlerRegistration = EventHelper.updateFocusHandler(this,
                focusHandlerRegistration, getWidget().getSelect());
        blurHandlerRegistration = EventHelper.updateBlurHandler(this,
                blurHandlerRegistration, getWidget().getSelect());
    }

    @Override
    public VNativeSelect getWidget() {
        return (VNativeSelect) super.getWidget();
    }

    @Override
    public void onFocus(FocusEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        getRpcProxy(FocusAndBlurServerRpc.class).focus();
    }

    @Override
    public void onBlur(BlurEvent event) {
        // EventHelper.updateFocusHandler ensures that this is called only when
        // there is a listener on server side
        getRpcProxy(FocusAndBlurServerRpc.class).blur();
    }

}
