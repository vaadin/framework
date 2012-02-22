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

public class VPopupCalendarPaintable extends VTextualDatePaintable {

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
        boolean lastReadOnlyState = getWidgetForPaintable().readonly;
        boolean lastEnabledState = getWidgetForPaintable().isEnabled();

        getWidgetForPaintable().parsable = uidl.getBooleanAttribute("parsable");

        super.updateFromUIDL(uidl, client);

        String popupStyleNames = getStyleNameFromUIDL(
                VPopupCalendar.POPUP_PRIMARY_STYLE_NAME, uidl, getState(),
                false);
        popupStyleNames += " "
                + VDateField.CLASSNAME
                + "-"
                + VPopupCalendar
                        .resolutionToString(getWidgetForPaintable().currentResolution);
        getWidgetForPaintable().popup.setStyleName(popupStyleNames);

        getWidgetForPaintable().calendar
                .setDateTimeService(getWidgetForPaintable()
                        .getDateTimeService());
        getWidgetForPaintable().calendar
                .setShowISOWeekNumbers(getWidgetForPaintable()
                        .isShowISOWeekNumbers());
        if (getWidgetForPaintable().calendar.getResolution() != getWidgetForPaintable().currentResolution) {
            getWidgetForPaintable().calendar
                    .setResolution(getWidgetForPaintable().currentResolution);
            if (getWidgetForPaintable().calendar.getDate() != null) {
                getWidgetForPaintable().calendar
                        .setDate((Date) getWidgetForPaintable()
                                .getCurrentDate().clone());
                // force re-render when changing resolution only
                getWidgetForPaintable().calendar.renderCalendar();
            }
        }
        getWidgetForPaintable().calendarToggle
                .setEnabled(getWidgetForPaintable().enabled);

        if (getWidgetForPaintable().currentResolution <= VPopupCalendar.RESOLUTION_MONTH) {
            getWidgetForPaintable().calendar
                    .setFocusChangeListener(new FocusChangeListener() {
                        public void focusChanged(Date date) {
                            getWidgetForPaintable().updateValue(date);
                            getWidgetForPaintable().buildDate();
                            Date date2 = getWidgetForPaintable().calendar
                                    .getDate();
                            date2.setYear(date.getYear());
                            date2.setMonth(date.getMonth());
                        }
                    });
        } else {
            getWidgetForPaintable().calendar.setFocusChangeListener(null);
        }

        if (getWidgetForPaintable().currentResolution > VPopupCalendar.RESOLUTION_DAY) {
            getWidgetForPaintable().calendar
                    .setTimeChangeListener(new TimeChangeListener() {
                        public void changed(int hour, int min, int sec, int msec) {
                            Date d = getWidgetForPaintable().getDate();
                            if (d == null) {
                                // date currently null, use the value from
                                // calendarPanel
                                // (~ client time at the init of the widget)
                                d = (Date) getWidgetForPaintable().calendar
                                        .getDate().clone();
                            }
                            d.setHours(hour);
                            d.setMinutes(min);
                            d.setSeconds(sec);
                            DateTimeService.setMilliseconds(d, msec);

                            // Always update time changes to the server
                            getWidgetForPaintable().updateValue(d);

                            // Update text field
                            getWidgetForPaintable().buildDate();
                        }
                    });
        }

        if (getWidgetForPaintable().readonly) {
            getWidgetForPaintable().calendarToggle
                    .addStyleName(VPopupCalendar.CLASSNAME + "-button-readonly");
        } else {
            getWidgetForPaintable().calendarToggle
                    .removeStyleName(VPopupCalendar.CLASSNAME
                            + "-button-readonly");
        }

        if (lastReadOnlyState != getWidgetForPaintable().readonly
                || lastEnabledState != getWidgetForPaintable().isEnabled()) {
            // Enabled or readonly state changed. Differences in theming might
            // affect the width (for instance if the popup button is hidden) so
            // we have to recalculate the width (IF the width of the field is
            // fixed)
            getWidgetForPaintable().updateWidth();
        }

        getWidgetForPaintable().calendarToggle.setEnabled(true);
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VPopupCalendar.class);
    }

    @Override
    public VPopupCalendar getWidgetForPaintable() {
        return (VPopupCalendar) super.getWidgetForPaintable();
    }
}
