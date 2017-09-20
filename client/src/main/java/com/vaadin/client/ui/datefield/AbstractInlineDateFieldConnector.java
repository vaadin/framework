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
package com.vaadin.client.ui.datefield;

import java.util.Date;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.VAbstractCalendarPanel;
import com.vaadin.client.ui.VAbstractCalendarPanel.FocusChangeListener;
import com.vaadin.client.ui.VAbstractDateFieldCalendar;
import com.vaadin.shared.ui.datefield.InlineDateFieldState;

/**
 * Base class for inline data field connector.
 *
 * @author Vaadin Ltd
 *
 * @param <R>
 *            the resolution type which the field is based on (day, month, ...)
 * @param <PANEL>
 *            Subclass of VAbstractCalendarPanel specific for the implementation
 * @since 8.0
 */
public abstract class AbstractInlineDateFieldConnector<PANEL extends VAbstractCalendarPanel<R>, R extends Enum<R>>
        extends AbstractDateFieldConnector<R> {

    @Override
    @SuppressWarnings("deprecation")
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().calendarPanel
                .setShowISOWeekNumbers(getWidget().isShowISOWeekNumbers());
        getWidget().calendarPanel
                .setDateTimeService(getWidget().getDateTimeService());
        getWidget().calendarPanel
                .setResolution(getWidget().getCurrentResolution());
        Date currentDate = getWidget().getCurrentDate();
        if (currentDate != null) {
            getWidget().calendarPanel.setDate(new Date(currentDate.getTime()));
        } else {
            getWidget().calendarPanel.setDate(null);
        }

        updateListeners();

        // Update possible changes
        getWidget().calendarPanel.renderCalendar();
    }

    /**
     * Updates listeners registered (or register them) for the widget based on
     * the current resolution.
     * <p>
     * Subclasses may override this method to keep the common logic inside the
     * {@link #updateFromUIDL(UIDL, ApplicationConnection)} method as is and
     * customizing only listeners logic.
     */
    protected void updateListeners() {
        if (isResolutionMonthOrHigher()) {
            getWidget().calendarPanel
                    .setFocusChangeListener(new FocusChangeListener() {
                        @Override
                        public void focusChanged(Date date) {
                            Date date2 = new Date();
                            if (getWidget().calendarPanel.getDate() != null) {
                                date2.setTime(getWidget().calendarPanel
                                        .getDate().getTime());
                            }
                            /*
                             * Update the value of calendarPanel
                             */
                            date2.setYear(date.getYear());
                            date2.setMonth(date.getMonth());
                            getWidget().calendarPanel.setDate(date2);
                            /*
                             * Then update the value from panel to server
                             */
                            getWidget().updateValueFromPanel();
                        }
                    });
        } else {
            getWidget().calendarPanel.setFocusChangeListener(null);
        }
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        getWidget().setTabIndex(getState().tabIndex);
        getWidget().calendarPanel.setRangeStart(getState().rangeStart);
        getWidget().calendarPanel.setRangeEnd(getState().rangeEnd);
    }

    @Override
    public VAbstractDateFieldCalendar<PANEL, R> getWidget() {
        return (VAbstractDateFieldCalendar<PANEL, R>) super.getWidget();
    }

    @Override
    public InlineDateFieldState getState() {
        return (InlineDateFieldState) super.getState();
    }

    /**
     * Returns {@code true} is the current resolution of the widget is month or
     * less specific (e.g. month, year, quarter, etc).
     *
     * @return {@code true} if the current resolution is above month
     */
    protected abstract boolean isResolutionMonthOrHigher();

}
