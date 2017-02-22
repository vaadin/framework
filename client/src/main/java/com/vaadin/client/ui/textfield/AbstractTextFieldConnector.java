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

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.DeferredWorker;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.AbstractTextFieldWidget;
import com.vaadin.client.ui.ConnectorFocusAndBlurHandler;
import com.vaadin.shared.ui.textfield.AbstractTextFieldClientRpc;
import com.vaadin.shared.ui.textfield.AbstractTextFieldServerRpc;
import com.vaadin.shared.ui.textfield.AbstractTextFieldState;
import com.vaadin.ui.AbstractTextField;

/**
 * Connector class for AbstractTextField.
 * 
 * @since 8.0
 */
public abstract class AbstractTextFieldConnector extends AbstractFieldConnector
        implements ValueChangeHandler.Owner, DeferredWorker {

    private class AbstractTextFieldClientRpcImpl
            implements AbstractTextFieldClientRpc {
        @Override
        public void selectRange(int start, int length) {
            int textLength = getAbstractTextField().getValue().length();
            start = restrictTo(start, 0, textLength - 1);
            length = restrictTo(length, 0, textLength - start);
            getAbstractTextField().setSelectionRange(start, length);
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
            getAbstractTextField().selectAll();
        }
    }

    private int lastSentCursorPosition = -1;
    private ValueChangeHandler valueChangeHandler;

    @Override
    protected void init() {
        registerRpc(AbstractTextFieldClientRpc.class,
                new AbstractTextFieldClientRpcImpl());
        ConnectorFocusAndBlurHandler.addHandlers(this);
        valueChangeHandler = new ValueChangeHandler(this);
    }

    protected ValueChangeHandler getValueChangeHandler() {
        return valueChangeHandler;
    }

    /**
     * Helper to cast {@link #getWidget()} to {@link AbstractTextField}. The
     * method exists only because getWidget() must return a {@link Widget} and
     * not an interface.
     *
     * @return the widget as an AbstractTextFieldWidget
     */
    private AbstractTextFieldWidget getAbstractTextField() {
        return (AbstractTextFieldWidget) getWidget();
    }

    @Override
    public AbstractTextFieldState getState() {
        return (AbstractTextFieldState) super.getState();
    }

    @OnStateChange("valueChangeMode")
    private void updateValueChangeMode() {
        valueChangeHandler.setValueChangeMode(getState().valueChangeMode);
    }

    @OnStateChange("valueChangeTimeout")
    private void updateValueChangeTimeout() {
        valueChangeHandler.setValueChangeTimeout(getState().valueChangeTimeout);
    }

    @OnStateChange("readOnly")
    private void updateReadOnly() {
        getAbstractTextField().setReadOnly(getState().readOnly);
    }

    private boolean hasStateChanged() {
        boolean textChanged = !getAbstractTextField().getValue()
                .equals(getState().text);
        boolean cursorPosChanged = getAbstractTextField()
                .getCursorPos() != lastSentCursorPosition;
        return textChanged || cursorPosChanged;
    }

    /**
     * Sends the updated value and cursor position to the server, if either one
     * has changed.
     */
    @Override
    public void sendValueChange() {
        if (!hasStateChanged()) {
            return;
        }

        lastSentCursorPosition = getAbstractTextField().getCursorPos();
        getRpcProxy(AbstractTextFieldServerRpc.class).setText(
                getAbstractTextField().getValue(), lastSentCursorPosition);
        getState().text = getAbstractTextField().getValue();
    }

    @Override
    public void flush() {
        super.flush();
        sendValueChange();
    }

    /**
     * {@inheritDoc}
     *
     * @since 8.0
     */
    @Override
    public boolean isWorkPending() {
        return getValueChangeHandler().isScheduled();
    }

}
