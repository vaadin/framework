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

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Event in the calendar. Customize your own event by implementing this
 * interface.
 * </p>
 * 
 * <li>Start and end fields are mandatory.</li>
 * 
 * <li>In "allDay" events longer than one day, starting and ending clock times
 * are omitted in UI and only dates are shown.</li>
 * 
 * @since 7.1.0
 * @author Vaadin Ltd.
 * 
 */
public interface CalendarEvent extends Serializable {

    /**
     * Gets start date of event.
     * 
     * @return Start date.
     */
    public Date getStart();

    /**
     * Get end date of event.
     * 
     * @return End date;
     */
    public Date getEnd();

    /**
     * Gets caption of event.
     * 
     * @return Caption text
     */
    public String getCaption();

    /**
     * Gets description of event. Shown as a tooltip over the event.
     * 
     * @return Description text.
     */
    public String getDescription();

    /**
     * <p>
     * Gets style name of event. In the client, style name will be set to the
     * event's element class name and can be styled by CSS
     * </p>
     * Styling example:</br> <code>Java code: </br>
     * event.setStyleName("color1");
     * </br></br>
     * CSS:</br>
     * .v-calendar-event-color1 {</br>
     * &nbsp;&nbsp;&nbsp;background-color: #9effae;</br>}</code>
     * 
     * @return Style name.
     */
    public String getStyleName();

    /**
     * An all-day event typically does not occur at a specific time but targets
     * a whole day or days. The rendering of all-day events differs from normal
     * events.
     * 
     * @return true if this event is an all-day event, false otherwise
     */
    public boolean isAllDay();

    /**
     * Event to signal that an event has changed.
     */
    @SuppressWarnings("serial")
    public class EventChangeEvent implements Serializable {

        private CalendarEvent source;

        public EventChangeEvent(CalendarEvent source) {
            this.source = source;
        }

        /**
         * @return the {@link com.vaadin.addon.calendar.event.CalendarEvent
         *         CalendarEvent} that has changed
         */
        public CalendarEvent getCalendarEvent() {
            return source;
        }
    }

    /**
     * Listener for EventSetChange events.
     */
    public interface EventChangeListener extends Serializable {

        /**
         * Called when an Event has changed.
         */
        public void eventChange(EventChangeEvent eventChangeEvent);
    }

    /**
     * Notifier interface for EventChange events.
     */
    public interface EventChangeNotifier extends Serializable {

        /**
         * Add a listener to listen for EventChangeEvents. These events are
         * fired when a events properties are changed.
         * 
         * @param listener
         *            The listener to add
         */
        void addEventChangeListener(EventChangeListener listener);

        /**
         * Remove a listener from the event provider.
         * 
         * @param listener
         *            The listener to remove
         */
        void removeEventChangeListener(EventChangeListener listener);
    }

}
