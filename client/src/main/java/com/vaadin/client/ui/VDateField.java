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

    @Deprecated
    public static final Resolution RESOLUTION_YEAR = Resolution.YEAR;
    @Deprecated
    public static final Resolution RESOLUTION_MONTH = Resolution.MONTH;
    @Deprecated
    public static final Resolution RESOLUTION_DAY = Resolution.DAY;
    @Deprecated
    public static final Resolution RESOLUTION_HOUR = Resolution.HOUR;
    @Deprecated
    public static final Resolution RESOLUTION_MIN = Resolution.MINUTE;
    @Deprecated
    public static final Resolution RESOLUTION_SEC = Resolution.SECOND;

    /** For internal use only. May be removed or replaced in the future. */
    public static String resolutionToString(Resolution res) {
        if (res.getCalendarField() > Resolution.DAY.getCalendarField()) {
            return "full";
        }
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
     * We need this redundant native function because Java's Date object doesn't
     * have a setMilliseconds method.
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public static native double getTime(int y, int m, int d, int h, int mi,
            int s, int ms)
    /*-{
       try {
       	var date = new Date(2000,1,1,1); // don't use current date here
       	if(y && y >= 0) date.setFullYear(y);
       	if(m && m >= 1) date.setMonth(m-1);
       	if(d && d >= 0) date.setDate(d);
       	if(h >= 0) date.setHours(h);
       	if(mi >= 0) date.setMinutes(mi);
       	if(s >= 0) date.setSeconds(s);
       	if(ms >= 0) date.setMilliseconds(ms);
       	return date.getTime();
       } catch (e) {
       	// TODO print some error message on the console
       	//console.log(e);
       	return (new Date()).getTime();
       }
    }-*/;

    public int getMilliseconds() {
        return DateTimeService.getMilliseconds(date);
    }

    public void setMilliseconds(int ms) {
        DateTimeService.setMilliseconds(date, ms);
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
