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

        String popupStyleNames = getStyleNameFromUIDL(
                VPopupCalendar.POPUP_PRIMARY_STYLE_NAME, uidl, false, this);
        popupStyleNames += " "
                + VDateField.CLASSNAME
                + "-"
                + VPopupCalendar
                        .resolutionToString(getWidget().currentResolution);
        getWidget().popup.setStyleName(popupStyleNames);

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
    protected Widget createWidget() {
        return GWT.create(VPopupCalendar.class);
    }

    @Override
    public VPopupCalendar getWidget() {
        return (VPopupCalendar) super.getWidget();
    }
}
