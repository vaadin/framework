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
package com.vaadin.client.ui;

import java.util.Objects;

import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.user.client.ui.ListBox;
import com.vaadin.client.widgets.FocusableFlowPanelComposite;
import com.vaadin.shared.ui.nativeselect.NativeSelectState;

/**
 * The client-side widget for the {@code NativeSelect} component.
 *
 * @author Vaadin Ltd.
 */
public class VNativeSelect extends FocusableFlowPanelComposite
        implements HasAllFocusHandlers {

    private final ListBox listBox = new ListBox();

    /**
     * Creates a new {@code VNativeSelect} instance.
     */
    public VNativeSelect() {
        setStyleName(NativeSelectState.STYLE_NAME);
        getListBox().setStyleName(NativeSelectState.STYLE_NAME + "-select");
        getWidget().add(listBox);
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        getListBox().setStyleName(style + "-select");
    }

    /**
     * Sets the selected item by its value. If given {@code null}, removes
     * selection.
     *
     * @param value
     *            the value of the item to select or {@code null} to select
     *            nothing
     */
    public void setSelectedItem(String value) {
        if (value == null) {
            getListBox().setSelectedIndex(-1);
        } else {
            for (int i = 0; i < getListBox().getItemCount(); i++) {
                if (Objects.equals(value, getListBox().getValue(i))) {
                    getListBox().setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /**
     * Sets the tab index.
     *
     * @param tabIndex
     *            the tab index to set
     */
    public void setTabIndex(int tabIndex) {
        getListBox().setTabIndex(tabIndex);
    }

    /**
     * Gets the underlying ListBox widget that this widget wraps.
     *
     * @return the ListBox this widget wraps
     */
    public ListBox getListBox() {
        return listBox;
    }

    @Override
    public void setWidth(String width) {
        if ("".equals(width)) {
            // undefined width
            getListBox().setWidth("");
        } else {
            // fill the composite
            getListBox().setWidth("100%");
        }
        super.setWidth(width);
    }

    @Override
    public void setHeight(String height) {
        if ("".equals(height)) {
            // undefined height
            getListBox().setHeight("");
        } else {
            // fill the composite
            getListBox().setHeight("100%");
        }
        super.setHeight(height);
    }

    /**
     * Sets the number of items that are visible. If only one item is visible,
     * then the box will be displayed as a drop-down list (the default).
     *
     * @since 8.1
     * @param visibleItemCount
     *            the visible item count
     */
    public void setVisibleItemCount(int visibleItemCount) {
        getListBox().setVisibleItemCount(visibleItemCount);
    }

    /**
     * Gets the number of items that are visible. If only one item is visible,
     * then the box will be displayed as a drop-down list.
     *
     * @since 8.1
     * @return the visible item count
     */
    public int getVisibleItemCount() {
        return getListBox().getVisibleItemCount();
    }

}
