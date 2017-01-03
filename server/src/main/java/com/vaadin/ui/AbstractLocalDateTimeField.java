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

import java.time.LocalDateTime;

import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.shared.ui.datefield.LocalDateTimeFieldState;

/**
 * @author Vaadin Ltd
 *
 */
public abstract class AbstractLocalDateTimeField
        extends AbstractDateField<LocalDateTime, DateTimeResolution> {

    /**
     * Constructs an empty <code>AbstractLocalDateTimeField</code> with no
     * caption.
     */
    public AbstractLocalDateTimeField() {
        super(DateTimeResolution.MINUTE);
    }

    /**
     * Constructs an empty <code>AbstractLocalDateTimeField</code> with caption.
     *
     * @param caption
     *            the caption of the datefield.
     */
    public AbstractLocalDateTimeField(String caption) {
        super(caption, DateTimeResolution.MINUTE);
    }

    @Override
    protected LocalDateTimeFieldState getState() {
        return (LocalDateTimeFieldState) super.getState();
    }

    @Override
    protected LocalDateTimeFieldState getState(boolean markAsDirty) {
        return (LocalDateTimeFieldState) super.getState(markAsDirty);
    }

}
