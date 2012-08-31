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
package com.vaadin.client.ui.datefield;

import java.util.Date;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.LocaleNotLoadedException;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.shared.ui.datefield.DateFieldConstants;

public class AbstractDateFieldConnector extends AbstractFieldConnector
        implements Paintable {

    @Override
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
                .getBooleanAttribute(DateFieldConstants.ATTR_WEEK_NUMBERS)
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

        // Remove old stylename that indicates current resolution
        setWidgetStyleName(
                VDateField.CLASSNAME
                        + "-"
                        + VDateField
                                .resolutionToString(getWidget().currentResolution),
                false);

        getWidget().currentResolution = newResolution;

        // Add stylename that indicates current resolution
        setWidgetStyleName(
                VDateField.CLASSNAME
                        + "-"
                        + VDateField
                                .resolutionToString(getWidget().currentResolution),
                true);

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
    public VDateField getWidget() {
        return (VDateField) super.getWidget();
    }
}
