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

package com.vaadin.client.ui.textfield;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.event.InputEvent;
import com.vaadin.client.event.InputHandler;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ConnectorFocusAndBlurHandler;
import com.vaadin.client.ui.VTextField;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.textfield.TextFieldServerRpc;
import com.vaadin.shared.ui.textfield.TextFieldState;
import com.vaadin.shared.ui.textfield.ValueChangeMode;
import com.vaadin.ui.TextField;

@Connect(value = TextField.class, loadStyle = LoadStyle.EAGER)
public class TextFieldConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        ConnectorFocusAndBlurHandler.addHandlers(this);
        getWidget().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                sendValueChange();
            }
        });
        getWidget().addDomHandler(new InputHandler() {

            @Override
            public void onInput(InputEvent event) {
                if (getState().valueChangeMode != ValueChangeMode.BLUR) {
                    scheduleValueChange();
                }
            }
        }, InputEvent.getType());
    }

    private Timer valueChangeTrigger = new Timer() {
        @Override
        public void run() {
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    sendValueChange();
                }
            });
        }
    };

    private void scheduleValueChange() {
        switch (getState().valueChangeMode) {
        case LAZY:
            lazyTextChange();
            break;
        case TIMEOUT:
            timeoutTextChange();
            break;
        case EAGER:
            eagerTextChange();
            break;
        }
    }

    private void lazyTextChange() {
        valueChangeTrigger.schedule(getState().valueChangeTimeout);
    }

    private void timeoutTextChange() {
        if (valueChangeTrigger.isRunning())
            return;
        valueChangeTrigger.schedule(getState().valueChangeTimeout);
    }

    private void eagerTextChange() {
        valueChangeTrigger.run();
    }

    @Override
    public TextFieldState getState() {
        return (TextFieldState) super.getState();
    }

    @Override
    public VTextField getWidget() {
        return (VTextField) super.getWidget();
    }

    @OnStateChange("readOnly")
    private void updateReadOnly() {
        getWidget().setReadOnly(getState().readOnly);
    }

    @OnStateChange({ "selectionStart", "selectionLength" })
    private void updateSelection() {
        if (getState().selectionStart != -1) {
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    getWidget().setSelectionRange(getState().selectionStart,
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
                getWidget().setCursorPos(getState().cursorPosition);
            }
        });
    }

    private boolean hasStateChanged() {
        boolean textChanged = !getWidget().getValue().equals(getState().text);
        boolean cursorPosChanged = getWidget()
                .getCursorPos() != getState().cursorPosition;
        return textChanged || cursorPosChanged;
    }

    private void sendValueChange() {
        if (!hasStateChanged()) {
            return;
        }
        getRpcProxy(TextFieldServerRpc.class).setText(getWidget().getValue(),
                getWidget().getCursorPos());
    }
}
