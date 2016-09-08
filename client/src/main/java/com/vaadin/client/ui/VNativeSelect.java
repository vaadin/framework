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

import com.google.gwt.user.client.ui.ListBox;
import com.vaadin.shared.ui.nativeselect.NativeSelectState;

/**
 * The client-side widget for the {@code NativeSelect} component.
 * 
 * @author Vaadin Ltd.
 */
public class VNativeSelect extends ListBox {

    /**
     * Creates a new {@code VNativeSelect} instance.
     */
    public VNativeSelect() {
        setStyleName(NativeSelectState.STYLE_NAME);
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
            setSelectedIndex(-1);
        } else {
            for (int i = 0; i < getItemCount(); i++) {
                if (Objects.equals(value, getValue(i))) {
                    setSelectedIndex(i);
                    break;
                }
            }
        }
    }
}
