/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.LocaleNotLoadedException;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;

public class VDateFieldPaintable extends VAbstractPaintableWidget {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Ensure correct implementation and let layout manage caption
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Save details
        getWidgetForPaintable().client = client;
        getWidgetForPaintable().paintableId = uidl.getId();
        getWidgetForPaintable().immediate = uidl
                .getBooleanAttribute("immediate");

        getWidgetForPaintable().readonly = uidl.getBooleanAttribute("readonly");
        getWidgetForPaintable().enabled = !uidl.getBooleanAttribute("disabled");

        if (uidl.hasAttribute("locale")) {
            final String locale = uidl.getStringAttribute("locale");
            try {
                getWidgetForPaintable().dts.setLocale(locale);
                getWidgetForPaintable().currentLocale = locale;
            } catch (final LocaleNotLoadedException e) {
                getWidgetForPaintable().currentLocale = getWidgetForPaintable().dts
                        .getLocale();
                VConsole.error("Tried to use an unloaded locale \"" + locale
                        + "\". Using default locale ("
                        + getWidgetForPaintable().currentLocale + ").");
                VConsole.error(e);
            }
        }

        // We show week numbers only if the week starts with Monday, as ISO 8601
        // specifies
        getWidgetForPaintable().showISOWeekNumbers = uidl
                .getBooleanAttribute(VDateField.WEEK_NUMBERS)
                && getWidgetForPaintable().dts.getFirstDayOfWeek() == 1;

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

        getWidgetForPaintable().currentResolution = newResolution;

        // Add stylename that indicates current resolution
        getWidgetForPaintable()
                .addStyleName(
                        VDateField.CLASSNAME
                                + "-"
                                + VDateField
                                        .resolutionToString(getWidgetForPaintable().currentResolution));

        final int year = uidl.getIntVariable("year");
        final int month = (getWidgetForPaintable().currentResolution >= VDateField.RESOLUTION_MONTH) ? uidl
                .getIntVariable("month") : -1;
        final int day = (getWidgetForPaintable().currentResolution >= VDateField.RESOLUTION_DAY) ? uidl
                .getIntVariable("day") : -1;
        final int hour = (getWidgetForPaintable().currentResolution >= VDateField.RESOLUTION_HOUR) ? uidl
                .getIntVariable("hour") : 0;
        final int min = (getWidgetForPaintable().currentResolution >= VDateField.RESOLUTION_MIN) ? uidl
                .getIntVariable("min") : 0;
        final int sec = (getWidgetForPaintable().currentResolution >= VDateField.RESOLUTION_SEC) ? uidl
                .getIntVariable("sec") : 0;

        // Construct new date for this datefield (only if not null)
        if (year > -1) {
            getWidgetForPaintable().setCurrentDate(
                    new Date((long) getWidgetForPaintable().getTime(year,
                            month, day, hour, min, sec, 0)));
        } else {
            getWidgetForPaintable().setCurrentDate(null);
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VDateField.class);
    }

    @Override
    public VDateField getWidgetForPaintable() {
        return (VDateField) super.getWidgetForPaintable();
    }
}
