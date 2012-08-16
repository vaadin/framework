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

package com.vaadin.terminal.gwt.client.ui.textarea;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.textarea.TextAreaState;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.textfield.TextFieldConnector;
import com.vaadin.ui.TextArea;

@Connect(TextArea.class)
public class TextAreaConnector extends TextFieldConnector {

    @Override
    public TextAreaState getState() {
        return (TextAreaState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().setRows(getState().getRows());
        getWidget().setWordwrap(getState().isWordwrap());
    }

    @Override
    public VTextArea getWidget() {
        return (VTextArea) super.getWidget();
    }
}
