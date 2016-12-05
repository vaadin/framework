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
package com.vaadin.ui;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcDecorator;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.TabIndexState;
import com.vaadin.ui.Component.Focusable;

/**
 * An abstract base class for focusable components. Includes API for setting the
 * tab index, programmatic focusing, and adding focus and blur listeners.
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public abstract class AbstractFocusable extends AbstractComponent
        implements Focusable, FocusNotifier, BlurNotifier {

    protected AbstractFocusable() {
        registerRpc(new FocusAndBlurServerRpcDecorator(this, this::fireEvent));
    }

    @Override
    public Registration addBlurListener(BlurListener listener) {
        return addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    @Override
    public Registration addFocusListener(FocusListener listener) {
        return addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    @Override
    public void focus() {
        super.focus();
    }

    @Override
    public int getTabIndex() {
        return getState(false).tabIndex;
    }

    @Override
    public void setTabIndex(int tabIndex) {
        getState().tabIndex = tabIndex;
    }

    @Override
    protected TabIndexState getState() {
        return (TabIndexState) super.getState();
    }

    @Override
    protected TabIndexState getState(boolean markAsDirty) {
        return (TabIndexState) super.getState(markAsDirty);
    }
}
