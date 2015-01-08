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
package com.vaadin.client.renderers;

import java.util.Date;

import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.vaadin.client.widget.grid.RendererCellReference;

/**
 * A renderer for rendering dates into cells
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DateRenderer implements Renderer<Date> {

    private DateTimeFormat format;

    // Calendar is unavailable for GWT
    @SuppressWarnings("deprecation")
    private TimeZone timeZone = TimeZone.createTimeZone(new Date()
            .getTimezoneOffset());

    public DateRenderer() {
        this(PredefinedFormat.DATE_TIME_SHORT);
    }

    public DateRenderer(PredefinedFormat format) {
        this(DateTimeFormat.getFormat(format));
    }

    public DateRenderer(DateTimeFormat format) {
        setFormat(format);
    }

    @Override
    public void render(RendererCellReference cell, Date date) {
        String dateStr = format.format(date, timeZone);
        cell.getElement().setInnerText(dateStr);
    }

    /**
     * Gets the format of how the date is formatted.
     * 
     * @return the format
     * @see <a
     *      href="http://www.gwtproject.org/javadoc/latest/com/google/gwt/i18n/shared/DateTimeFormat.html">GWT
     *      documentation on DateTimeFormat</a>
     */
    public DateTimeFormat getFormat() {
        return format;
    }

    /**
     * Sets the format used for formatting the dates.
     * 
     * @param format
     *            the format to set
     * @see <a
     *      href="http://www.gwtproject.org/javadoc/latest/com/google/gwt/i18n/shared/DateTimeFormat.html">GWT
     *      documentation on DateTimeFormat</a>
     */
    public void setFormat(DateTimeFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Format should not be null");
        }
        this.format = format;
    }

    /**
     * Returns the time zone of the date.
     * 
     * @return the time zone
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the time zone of the the date. By default uses the time zone of the
     * browser.
     * 
     * @param timeZone
     *            the timeZone to set
     */
    public void setTimeZone(TimeZone timeZone) {
        if (timeZone == null) {
            throw new IllegalArgumentException("Timezone should not be null");
        }
        this.timeZone = timeZone;
    }
}
