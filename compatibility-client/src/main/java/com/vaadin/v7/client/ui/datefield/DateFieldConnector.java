/*
 * Copyright 2000-2022 Vaadin Ltd.
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

package com.vaadin.v7.client.ui.datefield;

import java.util.Date;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.DateTimeService;
import com.vaadin.client.UIDL;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;
import com.vaadin.v7.client.ui.VCalendarPanel.FocusChangeListener;
import com.vaadin.v7.client.ui.VCalendarPanel.TimeChangeListener;
import com.vaadin.v7.client.ui.VPopupCalendar;
import com.vaadin.v7.shared.ui.datefield.PopupDateFieldState;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.DateField;

@Connect(DateField.class)
public class DateFieldConnector extends TextualDateConnector {

    @Override
    protected void init() {
        getWidget().popup.addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                /*
                 * FIXME This is a hack so we do not have to rewrite half of the
                 * datefield so values are not sent while selecting a date
                 * (#6252).
                 *
                 * The datefield will now only set the date UIDL variables while
                 * the user is selecting year/month/date/time and not send them
                 * directly. Only when the user closes the popup (by clicking on
                 * a day/enter/clicking outside of popup) then the new value is
                 * communicated to the server.
                 */
                if (getWidget().isImmediate()) {
                    getConnection().getServerRpcQueue().flush();
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.ui.VTextualDate#updateFromUIDL(com.vaadin
     * .client.UIDL, com.vaadin.client.ApplicationConnection)
     */
    @Override
    @SuppressWarnings("deprecation")
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        VPopupCalendar widget = getWidget();
        String oldLocale = widget.getCurrentLocale();

        widget.parsable = uidl.getBooleanAttribute("parsable");

        super.updateFromUIDL(uidl, client);

        widget.calendar.setDateTimeService(widget.getDateTimeService());
        widget.calendar
                .setShowISOWeekNumbers(widget.isShowISOWeekNumbers());
        if (widget.calendar.getResolution() != widget
                .getCurrentResolution()) {
            boolean hasSelectedDate = false;
            widget.calendar.setResolution(widget.getCurrentResolution());
            if (widget.calendar.getDate() != null
                    && widget.getCurrentDate() != null) {
                hasSelectedDate = true;
                widget.calendar
                        .setDate((Date) widget.getCurrentDate().clone());
            }
            // force re-render when changing resolution only
            widget.calendar.renderCalendar(hasSelectedDate);
        }

        // Force re-render of calendar if locale has changed (#12153)
        if (!widget.getCurrentLocale().equals(oldLocale)) {
            widget.calendar.renderCalendar();
        }

        if (widget.getCurrentResolution()
                .getCalendarField() <= Resolution.MONTH.getCalendarField()) {
            widget.calendar
                    .setFocusChangeListener(new FocusChangeListener() {
                        @Override
                        public void focusChanged(Date date) {

                            widget.updateValue(date);
                            widget.buildDate();
                            Date date2 = widget.calendar.getDate();
                            date2.setYear(date.getYear());
                            date2.setMonth(date.getMonth());
                        }
                    });
        } else {
            widget.calendar.setFocusChangeListener(null);
        }

        if (widget.getCurrentResolution()
                .getCalendarField() > Resolution.DAY.getCalendarField()) {
            widget.calendar
                    .setTimeChangeListener(new TimeChangeListener() {
                        @Override
                        public void changed(int hour, int min, int sec,
                                int msec) {
                            Date d = widget.getDate();
                            if (d == null) {
                                // date currently null, use the value from
                                // calendarPanel
                                // (~ client time at the init of the widget)
                                d = (Date) widget.calendar.getDate()
                                        .clone();
                            }
                            d.setHours(hour);
                            d.setMinutes(min);
                            d.setSeconds(sec);
                            DateTimeService.setMilliseconds(d, msec);

                            // Always update time changes to the server
                            widget.updateValue(d);

                            // Update text field
                            widget.buildDate();
                        }
                    });
        }

        if (widget.isReadonly()) {
            widget.calendarToggle.addStyleName(
                    VPopupCalendar.CLASSNAME + "-button-readonly");
        } else {
            widget.calendarToggle.removeStyleName(
                    VPopupCalendar.CLASSNAME + "-button-readonly");
        }

        widget.setDescriptionForAssistiveDevices(
                getState().descriptionForAssistiveDevices);

        widget.setTextFieldTabIndex();
    }

    @Override
    public VPopupCalendar getWidget() {
        return (VPopupCalendar) super.getWidget();
    }

    @Override
    public PopupDateFieldState getState() {
        return (PopupDateFieldState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        getWidget().setTextFieldEnabled(getState().textFieldEnabled);
        getWidget().setRangeStart(nullSafeDateClone(getState().rangeStart));
        getWidget().setRangeEnd(nullSafeDateClone(getState().rangeEnd));
    }

    private Date nullSafeDateClone(Date date) {
        if (date == null) {
            return null;
        } else {
            return (Date) date.clone();
        }
    }

    @Override
    protected void setWidgetStyleName(String styleName, boolean add) {
        super.setWidgetStyleName(styleName, add);

        // update the style change to popup calendar widget
        getWidget().popup.setStyleName(styleName, add);
    }

    @Override
    protected void setWidgetStyleNameWithPrefix(String prefix, String styleName,
            boolean add) {
        super.setWidgetStyleNameWithPrefix(prefix, styleName, add);

        // update the style change to popup calendar widget with the correct
        // prefix
        if (!styleName.startsWith("-")) {
            getWidget().popup.setStyleName(
                    getWidget().getStylePrimaryName() + "-popup-" + styleName,
                    add);
        } else {
            getWidget().popup.setStyleName(
                    getWidget().getStylePrimaryName() + "-popup" + styleName,
                    add);
        }
    }

}
