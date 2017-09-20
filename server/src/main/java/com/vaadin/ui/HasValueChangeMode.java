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

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.shared.ui.ValueChangeMode;

/**
 * Implemented by components which support value change modes.
 * 
 * @since 8.0
 */
public interface HasValueChangeMode extends Component {
    /**
     * Sets the mode how the TextField triggers {@link ValueChangeEvent}s.
     *
     * @param valueChangeMode
     *            the new mode
     *
     * @see ValueChangeMode
     */
    public void setValueChangeMode(ValueChangeMode valueChangeMode);

    /**
     * Returns the currently set {@link ValueChangeMode}.
     *
     * @return the mode used to trigger {@link ValueChangeEvent}s.
     *
     * @see ValueChangeMode
     */
    public ValueChangeMode getValueChangeMode();

    /**
     * Sets how often {@link ValueChangeEvent}s are triggered when the
     * {@link ValueChangeMode} is set to either {@link ValueChangeMode#LAZY} or
     * {@link ValueChangeMode#TIMEOUT}.
     *
     * @param valueChangeTimeout
     *            timeout in milliseconds, must be greater or equal to 0
     * @throws IllegalArgumentException
     *             if given timeout is smaller than 0
     *
     * @see ValueChangeMode
     */
    public void setValueChangeTimeout(int valueChangeTimeout);

    /**
     * Returns the currently set timeout, in milliseconds, for how often
     * {@link ValueChangeEvent}s are triggered if the current
     * {@link ValueChangeMode} is set to either {@link ValueChangeMode#LAZY} or
     * {@link ValueChangeMode#TIMEOUT}.
     *
     * @return the timeout in milliseconds of how often
     *         {@link ValueChangeEvent}s are triggered.
     */
    public int getValueChangeTimeout();

}
