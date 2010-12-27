/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.event.dom.client.DomEvent;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.DateTimeService;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VCalendarPanel.FocusChangeListener;
import com.vaadin.terminal.gwt.client.ui.VCalendarPanel.FocusOutListener;
import com.vaadin.terminal.gwt.client.ui.VCalendarPanel.SubmitListener;
import com.vaadin.terminal.gwt.client.ui.VCalendarPanel.TimeChangeListener;

/**
 * A client side implementation for InlineDateField
 */
public class VDateFieldCalendar extends VDateField {

    private final VCalendarPanel calendarPanel;

    public VDateFieldCalendar() {
        super();
        calendarPanel = new VCalendarPanel();
        add(calendarPanel);
        calendarPanel.setSubmitListener(new SubmitListener() {
            public void onSubmit() {
                updateValueFromPanel();
            }

            public void onCancel() {
                // TODO Auto-generated method stub

            }
        });
        calendarPanel.setFocusOutListener(new FocusOutListener() {
            public boolean onFocusOut(DomEvent<?> event) {
                updateValueFromPanel();
                return false;
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        calendarPanel.setShowISOWeekNumbers(isShowISOWeekNumbers());
        calendarPanel.setDateTimeService(getDateTimeService());
        calendarPanel.setResolution(getCurrentResolution());
        Date currentDate = getCurrentDate();
        if (currentDate != null) {
            calendarPanel.setDate(new Date(currentDate.getTime()));
        } else {
            calendarPanel.setDate(null);
        }

        if (currentResolution > RESOLUTION_DAY) {
            calendarPanel.setTimeChangeListener(new TimeChangeListener() {
                public void changed(int hour, int min, int sec, int msec) {
                    Date d = getDate();
                    d.setHours(hour);
                    d.setMinutes(min);
                    d.setSeconds(sec);
                    DateTimeService.setMilliseconds(d, msec);

                    // Always update time changes to the server
                    calendarPanel.setDate(d);
                    updateValueFromPanel();
                }
            });
        }

        if (currentResolution <= RESOLUTION_MONTH) {
            calendarPanel.setFocusChangeListener(new FocusChangeListener() {
                public void focusChanged(Date date) {
                    Date date2 = new Date();
                    if (calendarPanel.getDate() != null) {
                        date2.setTime(calendarPanel.getDate().getTime());
                    }
                    /*
                     * Update the value of calendarPanel
                     */
                    date2.setYear(date.getYear());
                    date2.setMonth(date.getMonth());
                    calendarPanel.setDate(date2);
                    /*
                     * Then update the value from panel to server
                     */
                    updateValueFromPanel();
                }
            });
        } else {
            calendarPanel.setFocusChangeListener(null);
        }

        // Update possible changes
        calendarPanel.renderCalendar();
    }

    /**
     * TODO refactor: almost same method as in VPopupCalendar.updateValue
     */
    @SuppressWarnings("deprecation")
    private void updateValueFromPanel() {
        Date date2 = calendarPanel.getDate();
        Date currentDate = getCurrentDate();
        if (currentDate == null || date2.getTime() != currentDate.getTime()) {
            setCurrentDate(date2);
            getClient().updateVariable(getId(), "year", date2.getYear() + 1900,
                    false);
            if (getCurrentResolution() > VDateField.RESOLUTION_YEAR) {
                getClient().updateVariable(getId(), "month",
                        date2.getMonth() + 1, false);
                if (getCurrentResolution() > RESOLUTION_MONTH) {
                    getClient().updateVariable(getId(), "day", date2.getDate(),
                            false);
                    if (getCurrentResolution() > RESOLUTION_DAY) {
                        getClient().updateVariable(getId(), "hour",
                                date2.getHours(), false);
                        if (getCurrentResolution() > RESOLUTION_HOUR) {
                            getClient().updateVariable(getId(), "min",
                                    date2.getMinutes(), false);
                            if (getCurrentResolution() > RESOLUTION_MIN) {
                                getClient().updateVariable(getId(), "sec",
                                        date2.getSeconds(), false);
                                if (getCurrentResolution() > RESOLUTION_SEC) {
                                    getClient().updateVariable(
                                            getId(),
                                            "msec",
                                            DateTimeService
                                                    .getMilliseconds(date2),
                                            false);
                                }
                            }
                        }
                    }
                }
            }
            if (isImmediate()) {
                getClient().sendPendingVariableChanges();
            }
        }
    }
}
