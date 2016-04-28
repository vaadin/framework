/*
 * Copyright 2000-2014 Vaadin Ltd.
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
import com.vaadin.client.ui.VDateField;
import com.vaadin.shared.ui.datefield.DateFieldConstants;
import com.vaadin.shared.ui.datefield.Resolution;

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
        getWidget().immediate = getState().immediate;

        getWidget().setReadonly(isReadOnly());
        getWidget().setEnabled(isEnabled());

        if (uidl.hasAttribute("locale")) {
            final String locale = uidl.getStringAttribute("locale");
            try {
                getWidget().dts.setLocale(locale);
                getWidget().setCurrentLocale(locale);
            } catch (final LocaleNotLoadedException e) {
                getWidget().setCurrentLocale(getWidget().dts.getLocale());
                VConsole.error("Tried to use an unloaded locale \"" + locale
                        + "\". Using default locale ("
                        + getWidget().getCurrentLocale() + ").");
                VConsole.error(e);
            }
        }

        // We show week numbers only if the week starts with Monday, as ISO 8601
        // specifies
        getWidget().setShowISOWeekNumbers(
                uidl.getBooleanAttribute(DateFieldConstants.ATTR_WEEK_NUMBERS)
                        && getWidget().dts.getFirstDayOfWeek() == 1);

        Resolution newResolution;
        if (uidl.hasVariable("sec")) {
            newResolution = Resolution.SECOND;
        } else if (uidl.hasVariable("min")) {
            newResolution = Resolution.MINUTE;
        } else if (uidl.hasVariable("hour")) {
            newResolution = Resolution.HOUR;
        } else if (uidl.hasVariable("day")) {
            newResolution = Resolution.DAY;
        } else if (uidl.hasVariable("month")) {
            newResolution = Resolution.MONTH;
        } else {
            newResolution = Resolution.YEAR;
        }

        // Remove old stylename that indicates current resolution
        setWidgetStyleName(
                getWidget().getStylePrimaryName()
                        + "-"
                        + VDateField.resolutionToString(getWidget()
                                .getCurrentResolution()), false);

        getWidget().setCurrentResolution(newResolution);

        // Add stylename that indicates current resolution
        setWidgetStyleName(
                getWidget().getStylePrimaryName()
                        + "-"
                        + VDateField.resolutionToString(getWidget()
                                .getCurrentResolution()), true);

        final Resolution resolution = getWidget().getCurrentResolution();
        final int year = uidl.getIntVariable("year");
        final int month = (resolution.getCalendarField() >= Resolution.MONTH
                .getCalendarField()) ? uidl.getIntVariable("month") : -1;
        final int day = (resolution.getCalendarField() >= Resolution.DAY
                .getCalendarField()) ? uidl.getIntVariable("day") : -1;
        final int hour = (resolution.getCalendarField() >= Resolution.HOUR
                .getCalendarField()) ? uidl.getIntVariable("hour") : 0;
        final int min = (resolution.getCalendarField() >= Resolution.MINUTE
                .getCalendarField()) ? uidl.getIntVariable("min") : 0;
        final int sec = (resolution.getCalendarField() >= Resolution.SECOND
                .getCalendarField()) ? uidl.getIntVariable("sec") : 0;

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
