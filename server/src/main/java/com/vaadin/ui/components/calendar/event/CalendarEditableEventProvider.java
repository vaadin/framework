/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.ui.components.calendar.event;

/**
 * An event provider which allows adding and removing events
 *
 * @since 7.1.0
 * @author Vaadin Ltd.
 */
public interface CalendarEditableEventProvider extends CalendarEventProvider {

    /**
     * Adds an event to the event provider
     *
     * @param event
     *            The event to add
     */
    void addEvent(CalendarEvent event);

    /**
     * Removes an event from the event provider
     *
     * @param event
     *            The event
     */
    void removeEvent(CalendarEvent event);
}
