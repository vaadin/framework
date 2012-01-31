/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.DateTimeService;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VCalendarPanel.FocusChangeListener;
import com.vaadin.terminal.gwt.client.ui.VCalendarPanel.TimeChangeListener;

public class VDateFieldCalendarPaintable extends VDateFieldPaintable {

    @Override
    @SuppressWarnings("deprecation")
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        getWidgetForPaintable().calendarPanel
                .setShowISOWeekNumbers(getWidgetForPaintable()
                        .isShowISOWeekNumbers());
        getWidgetForPaintable().calendarPanel
                .setDateTimeService(getWidgetForPaintable()
                        .getDateTimeService());
        getWidgetForPaintable().calendarPanel
                .setResolution(getWidgetForPaintable().getCurrentResolution());
        Date currentDate = getWidgetForPaintable().getCurrentDate();
        if (currentDate != null) {
            getWidgetForPaintable().calendarPanel.setDate(new Date(currentDate
                    .getTime()));
        } else {
            getWidgetForPaintable().calendarPanel.setDate(null);
        }

        if (getWidgetForPaintable().currentResolution > VDateField.RESOLUTION_DAY) {
            getWidgetForPaintable().calendarPanel
                    .setTimeChangeListener(new TimeChangeListener() {
                        public void changed(int hour, int min, int sec, int msec) {
                            Date d = getWidgetForPaintable().getDate();
                            if (d == null) {
                                // date currently null, use the value from
                                // calendarPanel
                                // (~ client time at the init of the widget)
                                d = (Date) getWidgetForPaintable().calendarPanel
                                        .getDate().clone();
                            }
                            d.setHours(hour);
                            d.setMinutes(min);
                            d.setSeconds(sec);
                            DateTimeService.setMilliseconds(d, msec);

                            // Always update time changes to the server
                            getWidgetForPaintable().calendarPanel.setDate(d);
                            getWidgetForPaintable().updateValueFromPanel();
                        }
                    });
        }

        if (getWidgetForPaintable().currentResolution <= VDateField.RESOLUTION_MONTH) {
            getWidgetForPaintable().calendarPanel
                    .setFocusChangeListener(new FocusChangeListener() {
                        public void focusChanged(Date date) {
                            Date date2 = new Date();
                            if (getWidgetForPaintable().calendarPanel.getDate() != null) {
                                date2.setTime(getWidgetForPaintable().calendarPanel
                                        .getDate().getTime());
                            }
                            /*
                             * Update the value of calendarPanel
                             */
                            date2.setYear(date.getYear());
                            date2.setMonth(date.getMonth());
                            getWidgetForPaintable().calendarPanel
                                    .setDate(date2);
                            /*
                             * Then update the value from panel to server
                             */
                            getWidgetForPaintable().updateValueFromPanel();
                        }
                    });
        } else {
            getWidgetForPaintable().calendarPanel.setFocusChangeListener(null);
        }

        // Update possible changes
        getWidgetForPaintable().calendarPanel.renderCalendar();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VDateFieldCalendar.class);
    }

    @Override
    public VDateFieldCalendar getWidgetForPaintable() {
        return (VDateFieldCalendar) super.getWidgetForPaintable();
    }
}
