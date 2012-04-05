/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.LocaleNotLoadedException;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;

public class AbstractDateFieldConnector extends AbstractFieldConnector
        implements Paintable {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }

        // Save details
        getWidget().client = client;
        getWidget().paintableId = uidl.getId();
        getWidget().immediate = getState().isImmediate();

        getWidget().readonly = isReadOnly();
        getWidget().enabled = isEnabled();

        if (uidl.hasAttribute("locale")) {
            final String locale = uidl.getStringAttribute("locale");
            try {
                getWidget().dts.setLocale(locale);
                getWidget().currentLocale = locale;
            } catch (final LocaleNotLoadedException e) {
                getWidget().currentLocale = getWidget().dts.getLocale();
                VConsole.error("Tried to use an unloaded locale \"" + locale
                        + "\". Using default locale ("
                        + getWidget().currentLocale + ").");
                VConsole.error(e);
            }
        }

        // We show week numbers only if the week starts with Monday, as ISO 8601
        // specifies
        getWidget().showISOWeekNumbers = uidl
                .getBooleanAttribute(VDateField.WEEK_NUMBERS)
                && getWidget().dts.getFirstDayOfWeek() == 1;

        int newResolution;
        if (uidl.hasVariable("sec")) {
            newResolution = VDateField.RESOLUTION_SEC;
        } else if (uidl.hasVariable("min")) {
            newResolution = VDateField.RESOLUTION_MIN;
        } else if (uidl.hasVariable("hour")) {
            newResolution = VDateField.RESOLUTION_HOUR;
        } else if (uidl.hasVariable("day")) {
            newResolution = VDateField.RESOLUTION_DAY;
        } else if (uidl.hasVariable("month")) {
            newResolution = VDateField.RESOLUTION_MONTH;
        } else {
            newResolution = VDateField.RESOLUTION_YEAR;
        }

        getWidget().currentResolution = newResolution;

        // Add stylename that indicates current resolution
        getWidget()
                .addStyleName(
                        VDateField.CLASSNAME
                                + "-"
                                + VDateField
                                        .resolutionToString(getWidget().currentResolution));

        final int year = uidl.getIntVariable("year");
        final int month = (getWidget().currentResolution >= VDateField.RESOLUTION_MONTH) ? uidl
                .getIntVariable("month") : -1;
        final int day = (getWidget().currentResolution >= VDateField.RESOLUTION_DAY) ? uidl
                .getIntVariable("day") : -1;
        final int hour = (getWidget().currentResolution >= VDateField.RESOLUTION_HOUR) ? uidl
                .getIntVariable("hour") : 0;
        final int min = (getWidget().currentResolution >= VDateField.RESOLUTION_MIN) ? uidl
                .getIntVariable("min") : 0;
        final int sec = (getWidget().currentResolution >= VDateField.RESOLUTION_SEC) ? uidl
                .getIntVariable("sec") : 0;

        // Construct new date for this datefield (only if not null)
        if (year > -1) {
            getWidget().setCurrentDate(
                    new Date((long) getWidget().getTime(year, month, day, hour,
                            min, sec, 0)));
        } else {
            getWidget().setCurrentDate(null);
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VDateField.class);
    }

    @Override
    public VDateField getWidget() {
        return (VDateField) super.getWidget();
    }
}
