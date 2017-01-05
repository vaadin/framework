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
package com.vaadin.client.ui.textfield;

import com.vaadin.client.event.InputEvent;
import com.vaadin.client.ui.VTextField;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.textfield.TextFieldState;
import com.vaadin.ui.TextField;

/**
 * Connector class for TextField.
 */
@Connect(value = TextField.class, loadStyle = LoadStyle.EAGER)
public class TextFieldConnector extends AbstractTextFieldConnector {

    @Override
    protected void init() {
        super.init();
        getWidget().addChangeHandler(event -> sendValueChange());
        getWidget().addDomHandler(event -> {
            getValueChangeHandler().scheduleValueChange();
        }, InputEvent.getType());
    }

    @Override
    public TextFieldState getState() {
        return (TextFieldState) super.getState();
    }

    @Override
    public VTextField getWidget() {
        return (VTextField) super.getWidget();
    }

}
