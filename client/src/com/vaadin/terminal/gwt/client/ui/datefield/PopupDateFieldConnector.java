/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.terminal.gwt.client.ui.datefield;

import java.util.Date;

import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.DateTimeService;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.datefield.VCalendarPanel.FocusChangeListener;
import com.vaadin.terminal.gwt.client.ui.datefield.VCalendarPanel.TimeChangeListener;
import com.vaadin.ui.DateField;

@Connect(DateField.class)
public class PopupDateFieldConnector extends TextualDateConnector {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.VTextualDate#updateFromUIDL(com.vaadin
     * .terminal.gwt.client.UIDL,
     * com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    @Override
    @SuppressWarnings("deprecation")
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        boolean lastReadOnlyState = getWidget().readonly;
        boolean lastEnabledState = getWidget().isEnabled();

        getWidget().parsable = uidl.getBooleanAttribute("parsable");

        super.updateFromUIDL(uidl, client);

        getWidget().calendar.setDateTimeService(getWidget()
                .getDateTimeService());
        getWidget().calendar.setShowISOWeekNumbers(getWidget()
                .isShowISOWeekNumbers());
        if (getWidget().calendar.getResolution() != getWidget().currentResolution) {
            getWidget().calendar.setResolution(getWidget().currentResolution);
            if (getWidget().calendar.getDate() != null) {
                getWidget().calendar.setDate((Date) getWidget()
                        .getCurrentDate().clone());
                // force re-render when changing resolution only
                getWidget().calendar.renderCalendar();
            }
        }
        getWidget().calendarToggle.setEnabled(getWidget().enabled);

        if (getWidget().currentResolution <= VPopupCalendar.RESOLUTION_MONTH) {
            getWidget().calendar
                    .setFocusChangeListener(new FocusChangeListener() {
                        @Override
                        public void focusChanged(Date date) {
                            getWidget().updateValue(date);
                            getWidget().buildDate();
                            Date date2 = getWidget().calendar.getDate();
                            date2.setYear(date.getYear());
                            date2.setMonth(date.getMonth());
                        }
                    });
        } else {
            getWidget().calendar.setFocusChangeListener(null);
        }

        if (getWidget().currentResolution > VPopupCalendar.RESOLUTION_DAY) {
            getWidget().calendar
                    .setTimeChangeListener(new TimeChangeListener() {
                        @Override
                        public void changed(int hour, int min, int sec, int msec) {
                            Date d = getWidget().getDate();
                            if (d == null) {
                                // date currently null, use the value from
                                // calendarPanel
                                // (~ client time at the init of the widget)
                                d = (Date) getWidget().calendar.getDate()
                                        .clone();
                            }
                            d.setHours(hour);
                            d.setMinutes(min);
                            d.setSeconds(sec);
                            DateTimeService.setMilliseconds(d, msec);

                            // Always update time changes to the server
                            getWidget().updateValue(d);

                            // Update text field
                            getWidget().buildDate();
                        }
                    });
        }

        if (getWidget().readonly) {
            getWidget().calendarToggle.addStyleName(VPopupCalendar.CLASSNAME
                    + "-button-readonly");
        } else {
            getWidget().calendarToggle.removeStyleName(VPopupCalendar.CLASSNAME
                    + "-button-readonly");
        }

        getWidget().calendarToggle.setEnabled(true);
    }

    @Override
    public VPopupCalendar getWidget() {
        return (VPopupCalendar) super.getWidget();
    }

    @Override
    protected void setWidgetStyleName(String styleName, boolean add) {
        super.setWidgetStyleName(styleName, add);

        // update the style change to popup calendar widget
        getWidget().popup.setStyleName(styleName, add);
    }

    @Override
    protected void setWidgetStyleNameWithPrefix(String prefix,
            String styleName, boolean add) {
        super.setWidgetStyleNameWithPrefix(prefix, styleName, add);

        // update the style change to popup calendar widget with the correct
        // prefix
        if (!styleName.startsWith("-")) {
            getWidget().popup.setStyleName(
                    VPopupCalendar.POPUP_PRIMARY_STYLE_NAME + "-" + styleName,
                    add);
        } else {
            getWidget().popup.setStyleName(
                    VPopupCalendar.POPUP_PRIMARY_STYLE_NAME + styleName, add);
        }
    }

}
