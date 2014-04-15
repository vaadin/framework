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
package com.vaadin.ui.components.calendar.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.components.calendar.event.CalendarEvent.EventChangeNotifier;

/**
 * Simple implementation of
 * {@link com.vaadin.addon.calendar.event.CalendarEvent CalendarEvent}. Has
 * setters for all required fields and fires events when this event is changed.
 * 
 * @since 7.1.0
 * @author Vaadin Ltd.
 */
@SuppressWarnings("serial")
public class BasicEvent implements EditableCalendarEvent, EventChangeNotifier {

    private String caption;
    private String description;
    private Date end;
    private Date start;
    private String styleName;
    private transient List<EventChangeListener> listeners = new ArrayList<EventChangeListener>();

    private boolean isAllDay;

    /**
     * Default constructor
     */
    public BasicEvent() {

    }

    /**
     * Constructor for creating an event with the same start and end date
     * 
     * @param caption
     *            The caption for the event
     * @param description
     *            The description for the event
     * @param date
     *            The date the event occurred
     */
    public BasicEvent(String caption, String description, Date date) {
        this.caption = caption;
        this.description = description;
        start = date;
        end = date;
    }

    /**
     * Constructor for creating an event with a start date and an end date.
     * Start date should be before the end date
     * 
     * @param caption
     *            The caption for the event
     * @param description
     *            The description for the event
     * @param startDate
     *            The start date of the event
     * @param endDate
     *            The end date of the event
     */
    public BasicEvent(String caption, String description, Date startDate,
            Date endDate) {
        this.caption = caption;
        this.description = description;
        start = startDate;
        end = endDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getCaption()
     */
    @Override
    public String getCaption() {
        return caption;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getEnd()
     */
    @Override
    public Date getEnd() {
        return end;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStart()
     */
    @Override
    public Date getStart() {
        return start;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStyleName()
     */
    @Override
    public String getStyleName() {
        return styleName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#isAllDay()
     */
    @Override
    public boolean isAllDay() {
        return isAllDay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setCaption(java.lang
     * .String)
     */
    @Override
    public void setCaption(String caption) {
        this.caption = caption;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setDescription(java
     * .lang.String)
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setEnd(java.util.
     * Date)
     */
    @Override
    public void setEnd(Date end) {
        this.end = end;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setStart(java.util
     * .Date)
     */
    @Override
    public void setStart(Date start) {
        this.start = start;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setStyleName(java
     * .lang.String)
     */
    @Override
    public void setStyleName(String styleName) {
        this.styleName = styleName;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setAllDay(boolean)
     */
    @Override
    public void setAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeListener
     * )
     */
    @Override
    public void addEventChangeListener(EventChangeListener listener) {
        listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeNotifier
     * #removeListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeListener
     * )
     */
    @Override
    public void removeEventChangeListener(EventChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fires an event change event to the listeners. Should be triggered when
     * some property of the event changes.
     */
    protected void fireEventChange() {
        EventChangeEvent event = new EventChangeEvent(this);

        for (EventChangeListener listener : listeners) {
            listener.eventChange(event);
        }
    }
}
