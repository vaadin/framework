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

package com.vaadin.ui;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcImpl;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;

/**
 * This is a simple drop-down select without, for instance, support for
 * multiselect, new items, lazyloading, and other advanced features. Sometimes
 * "native" select without all the bells-and-whistles of the ComboBox is a
 * better choice.
 */
@SuppressWarnings("serial")
public class NativeSelect extends AbstractSelect implements
        FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

    // width in characters, mimics TextField
    private int columns = 0;

    FocusAndBlurServerRpcImpl focusBlurRpc = new FocusAndBlurServerRpcImpl(this) {

        @Override
        protected void fireEvent(Event event) {
            NativeSelect.this.fireEvent(event);
        }
    };

    public NativeSelect() {
        super();
        registerRpc(focusBlurRpc);
    }

    public NativeSelect(String caption, Collection<?> options) {
        super(caption, options);
        registerRpc(focusBlurRpc);
    }

    public NativeSelect(String caption, Container dataSource) {
        super(caption, dataSource);
        registerRpc(focusBlurRpc);
    }

    public NativeSelect(String caption) {
        super(caption);
        registerRpc(focusBlurRpc);
    }

    /**
     * Sets the width of the component so that it can display approximately the
     * given number of letters.
     * <p>
     * Calling {@code setColumns(10);} is equivalent to calling
     * {@code setWidth("10em");}
     * </p>
     * 
     * @deprecated As of 7.0. "Columns" does not reflect the exact number of
     *             characters that will be displayed. It is better to use
     *             setWidth together with "em" to control the width of the
     *             field.
     * @param columns
     *            the number of columns to set.
     */
    @Deprecated
    public void setColumns(int columns) {
        if (columns < 0) {
            columns = 0;
        }
        if (this.columns != columns) {
            this.columns = columns;
            markAsDirty();
        }
    }

    /**
     * Gets the number of columns for the component.
     * 
     * @see #setColumns(int)
     * @deprecated As of 7.0. "Columns" does not reflect the exact number of
     *             characters that will be displayed. It is better to use
     *             setWidth together with "em" to control the width of the
     *             field.
     */
    @Deprecated
    public int getColumns() {
        return columns;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        target.addAttribute("type", "native");
        // Adds the number of columns
        if (columns != 0) {
            target.addAttribute("cols", columns);
        }

        super.paintContent(target);
    }

    @Override
    public void setMultiSelect(boolean multiSelect)
            throws UnsupportedOperationException {
        if (multiSelect == true) {
            throw new UnsupportedOperationException("Multiselect not supported");
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

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addFocusListener(FocusListener)}
     **/
    @Override
    @Deprecated
    public void addListener(FocusListener listener) {
        addFocusListener(listener);
    }

    @Override
    public void removeFocusListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeFocusListener(FocusListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(FocusListener listener) {
        removeFocusListener(listener);
    }

    @Override
    public void addBlurListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by {@link #addBlurListener(BlurListener)}
     **/
    @Override
    @Deprecated
    public void addListener(BlurListener listener) {
        addBlurListener(listener);
    }

    @Override
    public void removeBlurListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeBlurListener(BlurListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(BlurListener listener) {
        removeBlurListener(listener);
    }

}
