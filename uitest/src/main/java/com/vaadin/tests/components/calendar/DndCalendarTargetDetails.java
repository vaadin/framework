/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.components.calendar;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.table.DndTableTargetDetails;
import com.vaadin.ui.Calendar;

/**
 * Test UI for calendar as a drop target: CalendarTargetDetails should provide
 * getMouseEvent() method.
 * 
 * @author Vaadin Ltd
 */
public class DndCalendarTargetDetails extends DndTableTargetDetails {

    @Override
    protected void setup(VaadinRequest request) {
        createSourceTable();

        Calendar calendar = new Calendar();
        calendar.addStyleName("target");
        calendar.setDropHandler(new TestDropHandler());
        calendar.setWidth(100, Unit.PERCENTAGE);
        addComponent(calendar);
    }

    @Override
    protected String getTestDescription() {
        return "Mouse details should be available for CalendarTargetDetails DnD when calendar is a target";
    }

}
