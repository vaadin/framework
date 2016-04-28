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

import com.vaadin.ui.components.calendar.event.CalendarEvent.EventChangeEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider.EventSetChangeNotifier;

/**
 * <p>
 * Simple implementation of
 * {@link com.vaadin.addon.calendar.event.CalendarEventProvider
 * CalendarEventProvider}. Use {@link #addEvent(CalendarEvent)} and
 * {@link #removeEvent(CalendarEvent)} to add / remove events.
 * </p>
 * 
 * <p>
 * {@link com.vaadin.addon.calendar.event.CalendarEventProvider.EventSetChangeNotifier
 * EventSetChangeNotifier} and
 * {@link com.vaadin.addon.calendar.event.CalendarEvent.EventChangeListener
 * EventChangeListener} are also implemented, so the Calendar is notified when
 * an event is added, changed or removed.
 * </p>
 * 
 * @since 7.1.0
 * @author Vaadin Ltd.
 */
@SuppressWarnings("serial")
public class BasicEventProvider implements CalendarEditableEventProvider,
        EventSetChangeNotifier, CalendarEvent.EventChangeListener {

    protected List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();

    private List<EventSetChangeListener> listeners = new ArrayList<EventSetChangeListener>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventProvider#getEvents(java.
     * util.Date, java.util.Date)
     */
    @Override
    public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
        ArrayList<CalendarEvent> activeEvents = new ArrayList<CalendarEvent>();

        for (CalendarEvent ev : eventList) {
            long from = startDate.getTime();
            long to = endDate.getTime();

            if (ev.getStart() != null && ev.getEnd() != null) {
                long f = ev.getStart().getTime();
                long t = ev.getEnd().getTime();
                // Select only events that overlaps with startDate and
                // endDate.
                if ((f <= to && f >= from) || (t >= from && t <= to)
                        || (f <= from && t >= to)) {
                    activeEvents.add(ev);
                }
            }
        }

        return activeEvents;
    }

    /**
     * Does this event provider container this event
     * 
     * @param event
     *            The event to check for
     * @return If this provider has the event then true is returned, else false
     */
    public boolean containsEvent(BasicEvent event) {
        return eventList.contains(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventSetChangeNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventSetChangeListener
     * )
     */
    @Override
    public void addEventSetChangeListener(EventSetChangeListener listener) {
        listeners.add(listener);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventSetChangeNotifier
     * #removeListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventSetChangeListener
     * )
     */
    @Override
    public void removeEventSetChangeListener(EventSetChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fires a eventsetchange event. The event is fired when either an event is
     * added or removed to the event provider
     */
    protected void fireEventSetChange() {
        EventSetChangeEvent event = new EventSetChangeEvent(this);

        for (EventSetChangeListener listener : listeners) {
            listener.eventSetChange(event);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeListener
     * #eventChange
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventSetChange)
     */
    @Override
    public void eventChange(EventChangeEvent changeEvent) {
        // naive implementation
        fireEventSetChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEditableEventProvider#addEvent
     * (com.vaadin.addon.calendar.event.CalendarEvent)
     */
    @Override
    public void addEvent(CalendarEvent event) {
        eventList.add(event);
        if (event instanceof BasicEvent) {
            ((BasicEvent) event).addEventChangeListener(this);
        }
        fireEventSetChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEditableEventProvider#removeEvent
     * (com.vaadin.addon.calendar.event.CalendarEvent)
     */
    @Override
    public void removeEvent(CalendarEvent event) {
        eventList.remove(event);
        if (event instanceof BasicEvent) {
            ((BasicEvent) event).removeEventChangeListener(this);
        }
        fireEventSetChange();
    }
}
