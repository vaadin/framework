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
package com.vaadin.ui.components.colorpicker;

import java.io.Serializable;

import com.vaadin.shared.Registration;

public interface HasColorChangeListener extends Serializable {

    /**
     * Adds a {@link ColorChangeListener} to the component.
     *
     * @see Registration
     *
     * @param listener
     *            the listener to add, not null
     * @return a registration object for removing the listener
     */
    Registration addColorChangeListener(ColorChangeListener listener);

    /**
     * Removes a {@link ColorChangeListener} from the component.
     *
     * @param listener
     *            the listener to remove
     * 
     * @deprecated As of 8.0, replaced by {@link Registration#remove()} in the
     *             registration object returned from
     *             {@link #addColorChangeListener(ColorChangeListener)}.
     */
    @Deprecated
    void removeColorChangeListener(ColorChangeListener listener);

}
