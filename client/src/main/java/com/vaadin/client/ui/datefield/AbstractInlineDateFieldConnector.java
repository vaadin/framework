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
package com.vaadin.client.ui.datefield;

import java.util.Date;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.VAbstractCalendarPanel;
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

    /**
     * Updates listeners registered (or register them) for the widget based on
     * the current resolution.
     * <p>
     * Subclasses may override this method to keep the common logic inside the
     * {@link #updateFromUIDL(UIDL, ApplicationConnection)} method as is and
     * customizing only listeners logic.
     */
    @SuppressWarnings("deprecation")
    protected void updateListeners() {
        VAbstractDateFieldCalendar<PANEL, R> widget = getWidget();
        if (isResolutionMonthOrHigher()) {
            widget.calendarPanel.setFocusChangeListener(date -> {
                Date date2 = new Date();
                if (widget.calendarPanel.getDate() != null) {
                    date2.setTime(widget.calendarPanel.getDate().getTime());
                }
                /*
                 * Update the value of calendarPanel
                 */
                date2.setYear(date.getYear());
                date2.setMonth(date.getMonth());
                widget.calendarPanel.setDate(date2);
                /*
                 * Then update the value from panel to server
                 */
                widget.updateValueFromPanel();
            });
        } else {
            widget.calendarPanel.setFocusChangeListener(null);
        }
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        VAbstractDateFieldCalendar<PANEL, R> widget = getWidget();
        widget.setTabIndex(getState().tabIndex);
        widget.calendarPanel.setRangeStart(getState().rangeStart);
        widget.calendarPanel.setRangeEnd(getState().rangeEnd);

        widget.calendarPanel
                .setShowISOWeekNumbers(widget.isShowISOWeekNumbers());
        widget.calendarPanel.setDateTimeService(widget.getDateTimeService());
        widget.calendarPanel.setResolution(widget.getCurrentResolution());
        Date currentDate = widget.getCurrentDate();
        if (currentDate != null) {
            widget.calendarPanel.setDate(new Date(currentDate.getTime()));
        } else {
            widget.calendarPanel.setDate(null);
        }
        widget.calendarPanel.setDateStyles(getState().dateStyles);

        updateListeners();

        // Update possible changes
        widget.calendarPanel.renderCalendar();
    }

    @OnStateChange("assistiveLabels")
    private void updateAssistiveLabels() {
        setAndUpdateAssistiveLabels(getWidget().calendarPanel);
    }

    @Override
    @SuppressWarnings("unchecked")
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
