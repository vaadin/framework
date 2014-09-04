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
package com.vaadin.client.ui.calendar.schedule;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.vaadin.shared.ui.calendar.DateConstants;

/**
 * A client side implementation of a calendar event
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 */
public class CalendarEvent {
    private int index;
    private String caption;
    private Date start, end;
    private String styleName;
    private Date startTime, endTime;
    private String description;
    private int slotIndex = -1;
    private boolean format24h;

    DateTimeFormat dateformat_date = DateTimeFormat.getFormat("h:mm a");
    DateTimeFormat dateformat_date24 = DateTimeFormat.getFormat("H:mm");
    private boolean allDay;

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStyleName()
     */
    public String getStyleName() {
        return styleName;
    }

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStart()
     */
    public Date getStart() {
        return start;
    }

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStyleName()
     * @param style
     */
    public void setStyleName(String style) {
        styleName = style;
    }

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStart()
     * @param start
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getEnd()
     * @return
     */
    public Date getEnd() {
        return end;
    }

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getEnd()
     * @param end
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * Returns the start time of the event
     * 
     * @return Time embedded in the {@link Date} object
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Set the start time of the event
     * 
     * @param startTime
     *            The time of the event. Use the time fields in the {@link Date}
     *            object
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Get the end time of the event
     * 
     * @return Time embedded in the {@link Date} object
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Set the end time of the event
     * 
     * @param endTime
     *            Time embedded in the {@link Date} object
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Get the (server side) index of the event
     * 
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the index of the slot where the event in rendered
     * 
     * @return
     */
    public int getSlotIndex() {
        return slotIndex;
    }

    /**
     * Set the index of the slot where the event in rendered
     * 
     * @param index
     *            The index of the slot
     */
    public void setSlotIndex(int index) {
        slotIndex = index;
    }

    /**
     * Set the (server side) index of the event
     * 
     * @param index
     *            The index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Get the caption of the event. The caption is the text displayed in the
     * calendar on the event.
     * 
     * @return
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Set the caption of the event. The caption is the text displayed in the
     * calendar on the event.
     * 
     * @param caption
     *            The visible caption of the event
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Get the description of the event. The description is the text displayed
     * when hoovering over the event with the mouse
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the event. The description is the text displayed
     * when hoovering over the event with the mouse
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Does the event use the 24h time format
     * 
     * @param format24h
     *            True if it uses the 24h format, false if it uses the 12h time
     *            format
     */
    public void setFormat24h(boolean format24h) {
        this.format24h = format24h;
    }

    /**
     * Is the event an all day event.
     * 
     * @param allDay
     *            True if the event should be rendered all day
     */
    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    /**
     * Is the event an all day event.
     * 
     * @return
     */
    public boolean isAllDay() {
        return allDay;
    }

    /**
     * Get the time as a formatted string
     * 
     * @return
     */
    public String getTimeAsText() {
        if (format24h) {
            return dateformat_date24.format(startTime);
        } else {
            return dateformat_date.format(startTime);
        }
    }

    /**
     * Get the amount of milliseconds between the start and end of the event
     * 
     * @return
     */
    public long getRangeInMilliseconds() {
        return getEndTime().getTime() - getStartTime().getTime();
    }

    /**
     * Get the amount of minutes between the start and end of the event
     * 
     * @return
     */
    public long getRangeInMinutes() {
        return (getRangeInMilliseconds() / DateConstants.MINUTEINMILLIS);
    }

    /**
     * Get the amount of minutes for the event on a specific day. This is useful
     * if the event spans several days.
     * 
     * @param targetDay
     *            The date to check
     * @return
     */
    public long getRangeInMinutesForDay(Date targetDay) {
        long rangeInMinutesForDay = 0;
        // we must take into account that here can be not only 1 and 2 days, but
        // 1, 2, 3, 4... days first and last days - special cases all another
        // days between first and last - have range "ALL DAY"
        if (isTimeOnDifferentDays()) {
            if (targetDay.compareTo(getStart()) == 0) { // for first day
                rangeInMinutesForDay = DateConstants.DAYINMINUTES
                        - (getStartTime().getTime() - getStart().getTime())
                        / DateConstants.MINUTEINMILLIS;
            } else if (targetDay.compareTo(getEnd()) == 0) { // for last day
                rangeInMinutesForDay = (getEndTime().getTime() - getEnd()
                        .getTime()) / DateConstants.MINUTEINMILLIS;
            } else { // for in-between days
                rangeInMinutesForDay = DateConstants.DAYINMINUTES;
            }
        } else { // simple case - period is in one day
            rangeInMinutesForDay = getRangeInMinutes();
        }
        return rangeInMinutesForDay;
    }

    /**
     * Does the event span several days
     * 
     * @return
     */
    @SuppressWarnings("deprecation")
    public boolean isTimeOnDifferentDays() {
        boolean isSeveralDays = false;

        // if difference between start and end times is more than day - of
        // course it is not one day, but several days
        if (getEndTime().getTime() - getStartTime().getTime() > DateConstants.DAYINMILLIS) {
            isSeveralDays = true;
        } else { // if difference <= day -> there can be different cases
            if (getStart().compareTo(getEnd()) != 0
                    && getEndTime().compareTo(getEnd()) != 0) {
                isSeveralDays = true;
            }
        }
        return isSeveralDays;
    }
}
