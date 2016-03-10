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
package com.vaadin.tests.server.component.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.components.calendar.ContainerEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

/**
 * 
 * @author Vaadin Ltd
 */
public class ContainerEventProviderTest {

    @Test
    public void testDefaultAllDayProperty() {
        ContainerEventProvider provider = new ContainerEventProvider(null);
        Assert.assertEquals(ContainerEventProvider.ALL_DAY_PROPERTY,
                provider.getAllDayProperty());

    }

    @Test
    public void testSetAllDayProperty() {
        ContainerEventProvider provider = new ContainerEventProvider(null);
        Object prop = new Object();
        provider.setAllDayProperty(prop);
        Assert.assertEquals(prop, provider.getAllDayProperty());
    }

    @Test
    public void testGetEvents() {
        BeanItemContainer<EventBean> container = new BeanItemContainer<EventBean>(
                EventBean.class);
        EventBean bean = new EventBean();
        container.addBean(bean);
        ContainerEventProvider provider = new ContainerEventProvider(container);
        List<CalendarEvent> events = provider.getEvents(bean.getStart(),
                bean.getEnd());
        Assert.assertTrue(events.get(0).isAllDay());
    }

    public static class EventBean {

        public boolean isAllDay() {
            return true;
        }

        public void setAllDay(boolean allDay) {
        }

        public Date getStart() {
            return Calendar.getInstance().getTime();
        }

        public Date getEnd() {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 10);
            return calendar.getTime();
        }

        public void setStart(Date date) {
        }

        public void setEnd(Date date) {
        }
    }
}
