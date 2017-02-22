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

import com.vaadin.client.ui.textfield.AbstractTextFieldConnector;

/**
 * Implemented by all widgets used by a connector extending
 * {@link AbstractTextFieldConnector}.
 * 
 * @since 8.0
 */
public interface AbstractTextFieldWidget {

    /**
     * Sets the selection range for the field.
     *
     * @param start
     *            the start of the selection
     * @param length
     *            the length of the selection
     */
    public void setSelectionRange(int start, int length);

    /**
     * Gets the current value of the field.
     *
     * @return the current text in the field
     */
    public String getValue();

    /**
     * Selects all text in the field.
     */
    public void selectAll();

    /**
     * Sets the read-only mode of the field.
     *
     * @param readOnly
     *            <code>true</code> to set the field to read-only,
     *            <code>false</code> otherwise
     */
    public void setReadOnly(boolean readOnly);

    /**
     * Gets the current cursor position inside the field.
     *
     * @return the current cursor position
     */
    public int getCursorPos();

}
