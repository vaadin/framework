/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.client.ui.slider;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.slider.SliderServerRpc;
import com.vaadin.shared.ui.slider.SliderState;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractFieldConnector;
import com.vaadin.ui.Slider;

@Connect(Slider.class)
public class SliderConnector extends AbstractFieldConnector implements
        ValueChangeHandler<Double> {

    protected SliderServerRpc rpc = RpcProxy
            .create(SliderServerRpc.class, this);

    @Override
    public void init() {
        super.init();
        getWidget().setConnection(getConnection());
        getWidget().addValueChangeHandler(this);
    }

    @Override
    public VSlider getWidget() {
        return (VSlider) super.getWidget();
    }

    @Override
    public SliderState getState() {
        return (SliderState) super.getState();
    }

    @Override
    public void onValueChange(ValueChangeEvent<Double> event) {
        rpc.valueChanged(event.getValue());
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().setId(getConnectorId());
        getWidget().setImmediate(getState().isImmediate());
        getWidget().setDisabled(!isEnabled());
        getWidget().setReadOnly(isReadOnly());
        getWidget().setOrientation(getState().getOrientation());
        getWidget().setMinValue(getState().getMinValue());
        getWidget().setMaxValue(getState().getMaxValue());
        getWidget().setResolution(getState().getResolution());
        getWidget().setValue(getState().getValue(), false);
        getWidget().setFeedbackValue(getState().getValue());

        getWidget().buildBase();
    }

}
