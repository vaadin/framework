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

import com.google.gwt.event.dom.client.DomEvent;
import com.vaadin.client.ui.VAbstractCalendarPanel.FocusOutListener;
import com.vaadin.client.ui.VAbstractCalendarPanel.SubmitListener;

/**
 * A client side implementation for inline date field.
 */
public abstract class VAbstractDateFieldCalendar<PANEL extends VAbstractCalendarPanel<R>, R extends Enum<R>>
        extends VDateField<R> {

    /** For internal use only. May be removed or replaced in the future. */
    public final PANEL calendarPanel;

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
                // TODO Auto-generated method stub

            }
        });
        calendarPanel.setFocusOutListener(new FocusOutListener() {
            @Override
            public boolean onFocusOut(DomEvent<?> event) {
                updateValueFromPanel();
                return false;
            }
        });
    }

    @SuppressWarnings("deprecation")
    public abstract void updateValueFromPanel();

    public void setTabIndex(int tabIndex) {
        calendarPanel.getElement().setTabIndex(tabIndex);
    }

    public int getTabIndex() {
        return calendarPanel.getElement().getTabIndex();
    }
}
