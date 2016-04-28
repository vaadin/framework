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
package com.vaadin.client.ui.calendar.schedule.dd;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ui.calendar.CalendarConnector;
import com.vaadin.client.ui.dd.VAbstractDropHandler;

/**
 * Abstract base class for calendar drop handlers.
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 * 
 */
public abstract class CalendarDropHandler extends VAbstractDropHandler {

    protected final CalendarConnector calendarConnector;

    /**
     * Constructor
     * 
     * @param connector
     *            The connector of the calendar
     */
    public CalendarDropHandler(CalendarConnector connector) {
        calendarConnector = connector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#getConnector()
     */
    @Override
    public CalendarConnector getConnector() {
        return calendarConnector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VDropHandler#getApplicationConnection
     * ()
     */
    @Override
    public ApplicationConnection getApplicationConnection() {
        return calendarConnector.getClient();
    }

}
