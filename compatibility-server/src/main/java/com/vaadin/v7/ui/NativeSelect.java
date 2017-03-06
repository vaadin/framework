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

package com.vaadin.v7.ui;

import java.util.Collection;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcDecorator;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.event.FieldEvents;

/**
 * This is a simple drop-down select without, for instance, support for
 * multiselect, new items, lazyloading, and other advanced features. Sometimes
 * "native" select without all the bells-and-whistles of the ComboBox is a
 * better choice.
 *
 * @deprecated As of 8.0 replaced by {@link com.vaadin.ui.NativeSelect} based on
 *             the new data binding API
 */
@SuppressWarnings("serial")
@Deprecated
public class NativeSelect extends AbstractSelect
        implements FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

    public NativeSelect() {
        super();
        registerRpc(new FocusAndBlurServerRpcDecorator(this, this::fireEvent));
    }

    public NativeSelect(String caption, Collection<?> options) {
        super(caption, options);
        registerRpc(new FocusAndBlurServerRpcDecorator(this, this::fireEvent));
    }

    public NativeSelect(String caption, Container dataSource) {
        super(caption, dataSource);
        registerRpc(new FocusAndBlurServerRpcDecorator(this, this::fireEvent));
    }

    public NativeSelect(String caption) {
        super(caption);
        registerRpc(new FocusAndBlurServerRpcDecorator(this, this::fireEvent));
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "native");

        super.paintContent(target);
    }

    @Override
    public void setMultiSelect(boolean multiSelect)
            throws UnsupportedOperationException {
        if (multiSelect == true) {
            throw new UnsupportedOperationException(
                    "Multiselect not supported");
        }
    }

    @Override
    public void setNewItemsAllowed(boolean allowNewOptions)
            throws UnsupportedOperationException {
        if (allowNewOptions == true) {
            throw new UnsupportedOperationException(
                    "newItemsAllowed not supported");
        }
    }

    @Override
    public void addFocusListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    @Override
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    @Override
    public void addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    @Override
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

}
