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
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.DateTimeService;

/**
 * A very base widget class for a date field.
 * 
 * @author Vaadin Ltd
 *
 * @param <R>
 *            the resolution type which this field is based on (day, month, ...)
 */
public abstract class VDateField<R extends Enum<R>> extends FlowPanel
        implements Field, HasEnabled {

    public static final String CLASSNAME = "v-datefield";

    /** For internal use only. May be removed or replaced in the future. */
    public String paintableId;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    private R currentResolution;

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

    public VDateField(R resolution) {
        setStyleName(CLASSNAME);
        dts = new DateTimeService();
        currentResolution = resolution;
    }

    public R getCurrentResolution() {
        return currentResolution;
    }

    public void setCurrentResolution(R currentResolution) {
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

    /**
     * Set the current date using a map with date values.
     * <p>
     * The map contains integer representation of values per resolution. The
     * method should construct a date based on the map and set it via
     * {@link #setCurrentDate(Date)}
     * 
     * @param dateValues
     *            a map with date values to convert into a date
     */
    public void setCurrentDate(Map<R, Integer> dateValues) {
        setCurrentDate(getDate(dateValues));
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

    /**
     * Returns a resolution variable name for the given {@code resolution}.
     * 
     * @param resolution
     *            the given resolution
     * @return the resolution variable name
     */
    public String getResolutionVariable(R resolution) {
        return resolution.name().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Returns all available resolutions for the field in the ascending order
     * (which is the same as order of enumeration ordinals).
     * <p>
     * The method uses {@link #doGetResolutions()} to make sure that the order
     * is the correct one.
     * 
     * @see #doGetResolutions()
     * 
     * @return stream of all available resolutions in the ascending order.
     */
    public Stream<R> getResolutions() {
        return Stream.of(doGetResolutions()).sorted();
    }

    /**
     * Returns a current resolution as a string.
     * <p>
     * The method is used to generate a style name for the current resolution.
     * 
     * @return the current resolution as a string
     */
    public abstract String resolutionAsString();

    /**
     * Checks whether the given {@code resolution} represents an year.
     * 
     * @param resolution
     *            the given resolution
     * @return {@code true} if the {@code resolution} represents an year
     */
    public abstract boolean isYear(R resolution);

    /**
     * Returns a date based on the provided date values map.
     * 
     * @see #setCurrentDate(Map)
     * 
     * @param dateValues
     *            a map with date values to convert into a date
     * @return the date based on the dateValues map
     */
    protected abstract Date getDate(Map<R, Integer> dateValues);

    /**
     * Returns all available resolutions as an array.
     * <p>
     * No any order is required (in contrary to {@link #getResolutions()}.
     * 
     * @see #getResolutions()
     * 
     * @return all available resolutions
     */
    protected abstract R[] doGetResolutions();

}
