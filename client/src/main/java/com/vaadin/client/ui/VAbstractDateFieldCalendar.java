/*
 * Copyright 2000-2021 Vaadin Ltd.
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

import com.vaadin.client.ui.VAbstractCalendarPanel.SubmitListener;

/**
 * A client side implementation for inline date field.
 *
 * @param <PANEL>
 *            the calendar panel type this field uses
 * @param <R>
 *            the resolution type which this field is based on (day, month, ...)
 * @author Vaadin Ltd
 */
public abstract class VAbstractDateFieldCalendar<PANEL extends VAbstractCalendarPanel<R>, R extends Enum<R>>
        extends VDateField<R> {

    /** For internal use only. May be removed or replaced in the future. */
    public final PANEL calendarPanel;

    /**
     * Constructs a date selection widget with an inline date/time selector.
     *
     * @param panel
     *            the calendar panel instance that should be displayed
     * @param resolution
     *            the resolution this widget should display (day, month, ...)
     */
    public VAbstractDateFieldCalendar(PANEL panel, R resolution) {
        super(resolution);
        calendarPanel = panel;
        calendarPanel.setParentField(this);
        add(calendarPanel);
        calendarPanel.setSubmitListener(new SubmitListener() {
            @Override
            public void onSubmit() {
                updateValueFromPanel();
            }

            @Override
            public void onCancel() {
                // NOP
            }
        });
        calendarPanel.setFocusOutListener(event -> {
            updateValueFromPanel();
            return false;
        });
    }

    /**
     * Update buffered values and send them (if any) to the server.
     */
    public abstract void updateValueFromPanel();

    /**
     * Sets the tabulator index for the calendar panel element that represents
     * the entire widget in the browser's focus cycle.
     *
     * @param tabIndex
     *            the new tabulator index
     */
    public void setTabIndex(int tabIndex) {
        calendarPanel.getElement().setTabIndex(tabIndex);
    }

    /**
     * Returns the tabulator index of the calendar panel element that represents
     * the entire widget in the browser's focus cycle.
     *
     * @return the tabulator index
     */
    public int getTabIndex() {
        return calendarPanel.getElement().getTabIndex();
    }
}
