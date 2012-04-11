/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.datefield;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.DateTimeService;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.datefield.VCalendarPanel.FocusChangeListener;
import com.vaadin.terminal.gwt.client.ui.datefield.VCalendarPanel.TimeChangeListener;
import com.vaadin.ui.InlineDateField;

@Component(InlineDateField.class)
public class InlineDateFieldConnector extends AbstractDateFieldConnector {

    @Override
    @SuppressWarnings("deprecation")
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().calendarPanel.setShowISOWeekNumbers(getWidget()
                .isShowISOWeekNumbers());
        getWidget().calendarPanel.setDateTimeService(getWidget()
                .getDateTimeService());
        getWidget().calendarPanel.setResolution(getWidget()
                .getCurrentResolution());
        Date currentDate = getWidget().getCurrentDate();
        if (currentDate != null) {
            getWidget().calendarPanel.setDate(new Date(currentDate.getTime()));
        } else {
            getWidget().calendarPanel.setDate(null);
        }

        if (getWidget().currentResolution > VDateField.RESOLUTION_DAY) {
            getWidget().calendarPanel
                    .setTimeChangeListener(new TimeChangeListener() {
                        public void changed(int hour, int min, int sec, int msec) {
                            Date d = getWidget().getDate();
                            if (d == null) {
                                // date currently null, use the value from
                                // calendarPanel
                                // (~ client time at the init of the widget)
                                d = (Date) getWidget().calendarPanel.getDate()
                                        .clone();
                            }
                            d.setHours(hour);
                            d.setMinutes(min);
                            d.setSeconds(sec);
                            DateTimeService.setMilliseconds(d, msec);

                            // Always update time changes to the server
                            getWidget().calendarPanel.setDate(d);
                            getWidget().updateValueFromPanel();
                        }
                    });
        }

        if (getWidget().currentResolution <= VDateField.RESOLUTION_MONTH) {
            getWidget().calendarPanel
                    .setFocusChangeListener(new FocusChangeListener() {
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

        // Update possible changes
        getWidget().calendarPanel.renderCalendar();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VDateFieldCalendar.class);
    }

    @Override
    public VDateFieldCalendar getWidget() {
        return (VDateFieldCalendar) super.getWidget();
    }
}
