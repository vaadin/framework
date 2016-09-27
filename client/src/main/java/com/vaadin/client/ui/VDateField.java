/*
 * Copyright 2000-2016 Vaadin Ltd.
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

package com.vaadin.client.ui;

import java.util.Date;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.DateTimeService;
import com.vaadin.shared.ui.datefield.Resolution;

public class VDateField extends FlowPanel implements Field, HasEnabled {

    public static final String CLASSNAME = "v-datefield";

    /** For internal use only. May be removed or replaced in the future. */
    public String paintableId;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /** For internal use only. May be removed or replaced in the future. */
    public boolean immediate;

    /** For internal use only. May be removed or replaced in the future. */
    public static String resolutionToString(Resolution res) {
        if (res == Resolution.DAY) {
            return "day";
        }
        if (res == Resolution.MONTH) {
            return "month";
        }
        return "year";
    }

    protected Resolution currentResolution = Resolution.YEAR;

    protected String currentLocale;

    protected boolean readonly;

    protected boolean enabled;

    /**
     * The date that is selected in the date field. Null if an invalid date is
     * specified.
     */
    private Date date = null;

    /** For internal use only. May be removed or replaced in the future. */
    public DateTimeService dts;

    protected boolean showISOWeekNumbers = false;

    public VDateField() {
        setStyleName(CLASSNAME);
        dts = new DateTimeService();
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     */
    public static Date getTime(int year, int month, int day) {
        Date date = new Date(2000 - 1900, 0, 1);
        if (year >= 0) {
            date.setYear(year - 1900);
        }
        if (month >= 0) {
            date.setMonth(month - 1);
        }
        if (day >= 0) {
            date.setDate(day);
        }
        return date;
    }

    public Resolution getCurrentResolution() {
        return currentResolution;
    }

    public void setCurrentResolution(Resolution currentResolution) {
        this.currentResolution = currentResolution;
    }

    public String getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(String currentLocale) {
        this.currentLocale = currentLocale;
    }

    public Date getCurrentDate() {
        return date;
    }

    public void setCurrentDate(Date date) {
        this.date = date;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public DateTimeService getDateTimeService() {
        return dts;
    }

    public String getId() {
        return paintableId;
    }

    public ApplicationConnection getClient() {
        return client;
    }

    /**
     * Returns whether ISO 8601 week numbers should be shown in the date
     * selector or not. ISO 8601 defines that a week always starts with a Monday
     * so the week numbers are only shown if this is the case.
     *
     * @return true if week number should be shown, false otherwise
     */
    public boolean isShowISOWeekNumbers() {
        return showISOWeekNumbers;
    }

    public void setShowISOWeekNumbers(boolean showISOWeekNumbers) {
        this.showISOWeekNumbers = showISOWeekNumbers;
    }

    /**
     * Returns a copy of the current date. Modifying the returned date will not
     * modify the value of this VDateField. Use {@link #setDate(Date)} to change
     * the current date.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     *
     * @return A copy of the current date
     */
    public Date getDate() {
        Date current = getCurrentDate();
        if (current == null) {
            return null;
        } else {
            return (Date) getCurrentDate().clone();
        }
    }

    /**
     * Sets the current date for this VDateField.
     *
     * @param date
     *            The new date to use
     */
    protected void setDate(Date date) {
        this.date = date;
    }
}
