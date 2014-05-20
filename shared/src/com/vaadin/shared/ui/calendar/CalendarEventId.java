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
package com.vaadin.shared.ui.calendar;

import java.io.Serializable;

/**
 * CalendarEventId contains static String identifiers for all Calendar events.
 * These are used both in the client and server side code.
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 */
public class CalendarEventId implements Serializable {

    public static final String EVENTMOVE = "eventMove";
    public static final String RANGESELECT = "rangeSelect";
    public static final String FORWARD = "forward";
    public static final String BACKWARD = "backward";
    public static final String DATECLICK = "dateClick";
    public static final String WEEKCLICK = "weekClick";
    public static final String EVENTCLICK = "eventClick";
    public static final String EVENTRESIZE = "eventResize";
    public static final String ACTION = "action";
}
