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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.event.InputEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.ConnectorFocusAndBlurHandler;
import com.vaadin.client.ui.VTextField;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.textfield.AbstractTextFieldClientRpc;
import com.vaadin.shared.ui.textfield.AbstractTextFieldServerRpc;
import com.vaadin.shared.ui.textfield.TextFieldState;
import com.vaadin.shared.ui.textfield.ValueChangeMode;
import com.vaadin.ui.TextField;

/**
 * Connector class for TextField.
 */
@Connect(value = TextField.class, loadStyle = LoadStyle.EAGER)
public class TextFieldConnector extends AbstractComponentConnector {

    private class AbstractTextFieldClientRpcImpl
            implements AbstractTextFieldClientRpc {
        @Override
        public void selectRange(int start, int length) {
            int textLength = getWidget().getText().length();
            start = restrictTo(start, 0, textLength - 1);
            length = restrictTo(length, 0, textLength - start);
            getWidget().setSelectionRange(start, length);
        }

        private int restrictTo(int value, int min, int max) {
            if (value < min) {
                value = min;
            }
            if (value > max) {
                value = max;
            }

            return value;
        }

        @Override
        public void selectAll() {
            getWidget().selectAll();
        }
    }

    private int lastSentCursorPosition = -1;

    private Timer valueChangeTrigger = new Timer() {
        @Override
        public void run() {
            Scheduler.get().scheduleDeferred(() -> sendValueChange());
        }
    };

    @Override
    protected void init() {
        registerRpc(AbstractTextFieldClientRpc.class,
                new AbstractTextFieldClientRpcImpl());
        ConnectorFocusAndBlurHandler.addHandlers(this);
        getWidget().addChangeHandler(event -> sendValueChange());
        getWidget().addDomHandler(event -> {
            if (getState().valueChangeMode != ValueChangeMode.BLUR) {
                scheduleValueChange();
            }
        }, InputEvent.getType());
    }

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
        case BLUR:
            // Nothing to schedule for this mode
            break;
        }
    }

    private void lazyTextChange() {
        valueChangeTrigger.schedule(getState().valueChangeTimeout);
    }

    private void timeoutTextChange() {
        if (valueChangeTrigger.isRunning()) {
            return;
        }
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

    private boolean hasStateChanged() {
        boolean textChanged = !getWidget().getValue().equals(getState().text);
        boolean cursorPosChanged = getWidget()
                .getCursorPos() != lastSentCursorPosition;
        return textChanged || cursorPosChanged;
    }

    private void sendValueChange() {
        if (!hasStateChanged()) {
            return;
        }
        lastSentCursorPosition = getWidget().getCursorPos();
        getRpcProxy(AbstractTextFieldServerRpc.class)
                .setText(getWidget().getValue(), lastSentCursorPosition);
        getState().text = getWidget().getValue();
    }
}
