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
package com.vaadin.ui.components.calendar;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventMoveHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventResize;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventResizeHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.MoveEvent;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.CalendarEditableEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent.EventChangeListener;
import com.vaadin.ui.components.calendar.event.CalendarEvent.EventChangeNotifier;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider.EventSetChangeNotifier;

/**
 * A event provider which uses a {@link Container} as a datasource. Container
 * used as data source.
 * 
 * NOTE: The data source must be sorted by date!
 * 
 * @since 7.1.0
 * @author Vaadin Ltd.
 */
@SuppressWarnings("serial")
public class ContainerEventProvider implements CalendarEditableEventProvider,
        EventSetChangeNotifier, EventChangeNotifier, EventMoveHandler,
        EventResizeHandler, Container.ItemSetChangeListener,
        Property.ValueChangeListener {

    // Default property ids
    public static final String CAPTION_PROPERTY = "caption";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String STARTDATE_PROPERTY = "start";
    public static final String ENDDATE_PROPERTY = "end";
    public static final String STYLENAME_PROPERTY = "styleName";
    public static final String ALL_DAY_PROPERTY = "allDay";

    /**
     * Internal class to keep the container index which item this event
     * represents
     * 
     */
    private class ContainerCalendarEvent extends BasicEvent {
        private final int index;

        public ContainerCalendarEvent(int containerIndex) {
            super();
            index = containerIndex;
        }

        public int getContainerIndex() {
            return index;
        }
    }

    /**
     * Listeners attached to the container
     */
    private final List<EventSetChangeListener> eventSetChangeListeners = new LinkedList<CalendarEventProvider.EventSetChangeListener>();
    private final List<EventChangeListener> eventChangeListeners = new LinkedList<CalendarEvent.EventChangeListener>();

    /**
     * The event cache contains the events previously created by
     * {@link #getEvents(Date, Date)}
     */
    private final List<CalendarEvent> eventCache = new LinkedList<CalendarEvent>();

    /**
     * The container used as datasource
     */
    private Indexed container;

    /**
     * Container properties. Defaults based on using the {@link BasicEvent}
     * helper class.
     */
    private Object captionProperty = CAPTION_PROPERTY;
    private Object descriptionProperty = DESCRIPTION_PROPERTY;
    private Object startDateProperty = STARTDATE_PROPERTY;
    private Object endDateProperty = ENDDATE_PROPERTY;
    private Object styleNameProperty = STYLENAME_PROPERTY;
    private Object allDayProperty = ALL_DAY_PROPERTY;

    /**
     * Constructor
     * 
     * @param container
     *            Container to use as a data source.
     */
    public ContainerEventProvider(Container.Indexed container) {
        this.container = container;
        listenToContainerEvents();
    }

    /**
     * Set the container data source
     * 
     * @param container
     *            The container to use as datasource
     * 
     */
    public void setContainerDataSource(Container.Indexed container) {
        // Detach the previous container
        detachContainerDataSource();

        this.container = container;
        listenToContainerEvents();
    }

    /**
     * Returns the container used as data source
     * 
     */
    public Container.Indexed getContainerDataSource() {
        return container;
    }

    /**
     * Attaches listeners to the container so container events can be processed
     */
    private void listenToContainerEvents() {
        if (container instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) container).addItemSetChangeListener(this);
        }
        if (container instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) container).addValueChangeListener(this);
        }
    }

    /**
     * Removes listeners from the container so no events are processed
     */
    private void ignoreContainerEvents() {
        if (container instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) container)
                    .removeItemSetChangeListener(this);
        }
        if (container instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) container).removeValueChangeListener(this);
        }
    }

    /**
     * Converts an event in the container to an {@link CalendarEvent}
     * 
     * @param index
     *            The index of the item in the container to get the event for
     * @return
     */
    private CalendarEvent getEvent(int index) {

        // Check the event cache first
        for (CalendarEvent e : eventCache) {
            if (e instanceof ContainerCalendarEvent
                    && ((ContainerCalendarEvent) e).getContainerIndex() == index) {
                return e;
            } else if (container.getIdByIndex(index) == e) {
                return e;
            }
        }

        final Object id = container.getIdByIndex(index);
        Item item = container.getItem(id);
        CalendarEvent event;
        if (id instanceof CalendarEvent) {
            /*
             * If we are using the BeanItemContainer or another container which
             * stores the objects as ids then just return the instances
             */
            event = (CalendarEvent) id;

        } else {
            /*
             * Else we use the properties to create the event
             */
            BasicEvent basicEvent = new ContainerCalendarEvent(index);

            // Set values from property values
            if (captionProperty != null
                    && item.getItemPropertyIds().contains(captionProperty)) {
                basicEvent.setCaption(String.valueOf(item.getItemProperty(
                        captionProperty).getValue()));
            }
            if (descriptionProperty != null
                    && item.getItemPropertyIds().contains(descriptionProperty)) {
                basicEvent.setDescription(String.valueOf(item.getItemProperty(
                        descriptionProperty).getValue()));
            }
            if (startDateProperty != null
                    && item.getItemPropertyIds().contains(startDateProperty)) {
                basicEvent.setStart((Date) item.getItemProperty(
                        startDateProperty).getValue());
            }
            if (endDateProperty != null
                    && item.getItemPropertyIds().contains(endDateProperty)) {
                basicEvent.setEnd((Date) item.getItemProperty(endDateProperty)
                        .getValue());
            }
            if (styleNameProperty != null
                    && item.getItemPropertyIds().contains(styleNameProperty)) {
                basicEvent.setStyleName(String.valueOf(item.getItemProperty(
                        styleNameProperty).getValue()));
            }
            if (allDayProperty != null
                    && item.getItemPropertyIds().contains(allDayProperty)) {
                basicEvent.setAllDay((Boolean) item.getItemProperty(
                        allDayProperty).getValue());
            }
            event = basicEvent;
        }
        return event;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventProvider#getEvents(java.
     * util.Date, java.util.Date)
     */
    @Override
    public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
        eventCache.clear();
        int size = container.size();
        assert size >= 0;

        for (int i = 0; i < size; i++) {
            Object id = container.getIdByIndex(i);
            Item item = container.getItem(id);
            boolean add = true;
            if (startDate != null) {
                Date eventEnd = (Date) item.getItemProperty(endDateProperty)
                        .getValue();
                if (eventEnd.compareTo(startDate) < 0) {
                    add = false;
                }
            }
            if (add && endDate != null) {
                Date eventStart = (Date) item
                        .getItemProperty(startDateProperty).getValue();
                if (eventStart.compareTo(endDate) >= 0) {
                    break; // because container is sorted, all further events
                    // will be even later
                }
            }
            if (add) {
                eventCache.add(getEvent(i));
            }
        }
        return Collections.unmodifiableList(eventCache);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventProvider.EventSetChangeNotifier
     * #addListener(com.vaadin.addon.calendar.event.CalendarEventProvider.
     * EventSetChangeListener)
     */
    @Override
    public void addEventSetChangeListener(EventSetChangeListener listener) {
        if (!eventSetChangeListeners.contains(listener)) {
            eventSetChangeListeners.add(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventProvider.EventSetChangeNotifier
     * #removeListener(com.vaadin.addon.calendar.event.CalendarEventProvider.
     * EventSetChangeListener)
     */
    @Override
    public void removeEventSetChangeListener(EventSetChangeListener listener) {
        eventSetChangeListeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEvent.EventChangeNotifier#addListener
     * (com.vaadin.addon.calendar.event.CalendarEvent.EventChangeListener)
     */
    @Override
    public void addEventChangeListener(EventChangeListener listener) {
        if (eventChangeListeners.contains(listener)) {
            eventChangeListeners.add(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent.EventChangeNotifier#
     * removeListener
     * (com.vaadin.addon.calendar.event.CalendarEvent.EventChangeListener)
     */
    @Override
    public void removeEventChangeListener(EventChangeListener listener) {
        eventChangeListeners.remove(listener);
    }

    /**
     * Get the property which provides the caption of the event
     */
    public Object getCaptionProperty() {
        return captionProperty;
    }

    /**
     * Set the property which provides the caption of the event
     */
    public void setCaptionProperty(Object captionProperty) {
        this.captionProperty = captionProperty;
    }

    /**
     * Get the property which provides the description of the event
     */
    public Object getDescriptionProperty() {
        return descriptionProperty;
    }

    /**
     * Set the property which provides the description of the event
     */
    public void setDescriptionProperty(Object descriptionProperty) {
        this.descriptionProperty = descriptionProperty;
    }

    /**
     * Get the property which provides the starting date and time of the event
     */
    public Object getStartDateProperty() {
        return startDateProperty;
    }

    /**
     * Set the property which provides the starting date and time of the event
     */
    public void setStartDateProperty(Object startDateProperty) {
        this.startDateProperty = startDateProperty;
    }

    /**
     * Get the property which provides the ending date and time of the event
     */
    public Object getEndDateProperty() {
        return endDateProperty;
    }

    /**
     * Set the property which provides the ending date and time of the event
     */
    public void setEndDateProperty(Object endDateProperty) {
        this.endDateProperty = endDateProperty;
    }

    /**
     * Get the property which provides the style name for the event
     */
    public Object getStyleNameProperty() {
        return styleNameProperty;
    }

    /**
     * Set the property which provides the style name for the event
     */
    public void setStyleNameProperty(Object styleNameProperty) {
        this.styleNameProperty = styleNameProperty;
    }

    /**
     * Set the all day property for the event
     *
     * @since 7.3.4
     */
    public void setAllDayProperty(Object allDayProperty) {
        this.allDayProperty = allDayProperty;
    }

    /**
     * Get the all day property for the event
     *
     * @since 7.3.4
     */
    public Object getAllDayProperty() {
        return allDayProperty;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.ItemSetChangeListener#containerItemSetChange
     * (com.vaadin.data.Container.ItemSetChangeEvent)
     */
    @Override
    public void containerItemSetChange(ItemSetChangeEvent event) {
        if (event.getContainer() == container) {
            // Trigger an eventset change event when the itemset changes
            for (EventSetChangeListener listener : eventSetChangeListeners) {
                listener.eventSetChange(new EventSetChangeEvent(this));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data
     * .Property.ValueChangeEvent)
     */
    @Override
    public void valueChange(ValueChangeEvent event) {
        /*
         * TODO Need to figure out how to get the item which triggered the the
         * valuechange event and then trigger a EventChange event to the
         * listeners
         */
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveHandler
     * #eventMove
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent)
     */
    @Override
    public void eventMove(MoveEvent event) {
        CalendarEvent ce = event.getCalendarEvent();
        if (eventCache.contains(ce)) {
            int index;
            if (ce instanceof ContainerCalendarEvent) {
                index = ((ContainerCalendarEvent) ce).getContainerIndex();
            } else {
                index = container.indexOfId(ce);
            }

            long eventLength = ce.getEnd().getTime() - ce.getStart().getTime();
            Date newEnd = new Date(event.getNewStart().getTime() + eventLength);

            ignoreContainerEvents();
            Item item = container.getItem(container.getIdByIndex(index));
            item.getItemProperty(startDateProperty).setValue(
                    event.getNewStart());
            item.getItemProperty(endDateProperty).setValue(newEnd);
            listenToContainerEvents();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResizeHandler
     * #eventResize
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResize)
     */
    @Override
    public void eventResize(EventResize event) {
        CalendarEvent ce = event.getCalendarEvent();
        if (eventCache.contains(ce)) {
            int index;
            if (ce instanceof ContainerCalendarEvent) {
                index = ((ContainerCalendarEvent) ce).getContainerIndex();
            } else {
                index = container.indexOfId(ce);
            }
            ignoreContainerEvents();
            Item item = container.getItem(container.getIdByIndex(index));
            item.getItemProperty(startDateProperty).setValue(
                    event.getNewStart());
            item.getItemProperty(endDateProperty).setValue(event.getNewEnd());
            listenToContainerEvents();
        }
    }

    /**
     * If you are reusing the container which previously have been attached to
     * this ContainerEventProvider call this method to remove this event
     * providers container listeners before attaching it to an other
     * ContainerEventProvider
     */
    public void detachContainerDataSource() {
        ignoreContainerEvents();
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
        Item item;
        try {
            item = container.addItem(event);
        } catch (UnsupportedOperationException uop) {
            // Thrown if container does not support adding items with custom
            // ids. JPAContainer for example.
            item = container.getItem(container.addItem());
        }
        if (item != null) {
            item.getItemProperty(getCaptionProperty()).setValue(
                    event.getCaption());
            item.getItemProperty(getStartDateProperty()).setValue(
                    event.getStart());
            item.getItemProperty(getEndDateProperty()).setValue(event.getEnd());
            item.getItemProperty(getStyleNameProperty()).setValue(
                    event.getStyleName());
            item.getItemProperty(getDescriptionProperty()).setValue(
                    event.getDescription());
        }
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
        container.removeItem(event);
    }
}
