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

package com.vaadin.client.tokka.connectors.fields;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Command;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.tokka.ui.VTextField;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.tokka.ui.components.fields.TextFieldServerRpc;
import com.vaadin.shared.tokka.ui.components.fields.TextFieldState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.tokka.ui.components.fields.TextField;

@Connect(value = TextField.class, loadStyle = LoadStyle.EAGER)
public class TextFieldConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        getWidget().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                getRpcProxy(TextFieldServerRpc.class)
                .setText(getWidget().getValue());
            }

        });
        getWidget().addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                getRpcProxy(TextFieldServerRpc.class)
                .blur();
            }

        });
        getWidget().addFocusHandler(new FocusHandler() {

            @Override
            public void onFocus(FocusEvent event) {
                getRpcProxy(TextFieldServerRpc.class)
                .focus();
            }

        });
    }

    @Override
    public TextFieldState getState() {
        return (TextFieldState) super.getState();
    }

    @Override
    public VTextField getWidget() {
        return (VTextField) super.getWidget();
    }

    @OnStateChange("text")
    private void updateText() {
        getWidget().setValue(getState().text);
    }

    @OnStateChange("readOnly")
    private void updateReadOnly() {
        getWidget().setReadOnly(getState().readOnly);
    }

    @OnStateChange({"selectionStart", "selectionLength"})
    private void updateSelection() {
        if (getState().selectionStart != -1) {
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    getWidget()
                    .setSelectionRange(
                            getState().selectionStart,
                            getState().selectionLength);
                }
            });
        }
    }

    @OnStateChange("cursorPosition")
    private void updateCursorPosition() {
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                getWidget()
                .setCursorPos(getState().cursorPosition);
            }
        });
    }
}
