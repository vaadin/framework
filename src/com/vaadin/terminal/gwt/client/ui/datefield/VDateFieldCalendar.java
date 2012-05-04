/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.datefield;

import java.util.Date;

import com.google.gwt.event.dom.client.DomEvent;
import com.vaadin.terminal.gwt.client.DateTimeService;
import com.vaadin.terminal.gwt.client.ui.datefield.VCalendarPanel.FocusOutListener;
import com.vaadin.terminal.gwt.client.ui.datefield.VCalendarPanel.SubmitListener;

/**
 * A client side implementation for InlineDateField
 */
public class VDateFieldCalendar extends VDateField {

    protected final VCalendarPanel calendarPanel;

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

    /**
     * TODO refactor: almost same method as in VPopupCalendar.updateValue
     */
    @SuppressWarnings("deprecation")
    protected void updateValueFromPanel() {

        // If field is invisible at the beginning, client can still be null when
        // this function is called.
        if (getClient() == null) {
            return;
        }

        Date date2 = calendarPanel.getDate();
        Date currentDate = getCurrentDate();
        if (currentDate == null || date2.getTime() != currentDate.getTime()) {
            setCurrentDate((Date) date2.clone());
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
