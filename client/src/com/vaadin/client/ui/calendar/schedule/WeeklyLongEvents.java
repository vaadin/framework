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
import java.util.List;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.vaadin.client.ui.VCalendar;

/**
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 * 
 */
public class WeeklyLongEvents extends HorizontalPanel implements HasTooltipKey {

    public static final int EVENT_HEIGTH = 15;

    public static final int EVENT_MARGIN = 1;

    private int rowCount = 0;

    private VCalendar calendar;

    private boolean undefinedWidth;

    public WeeklyLongEvents(VCalendar calendar) {
        setStylePrimaryName("v-calendar-weekly-longevents");
        this.calendar = calendar;
    }

    public void addDate(Date d) {
        DateCellContainer dcc = new DateCellContainer();
        dcc.setDate(d);
        dcc.setCalendar(calendar);
        add(dcc);
    }

    public void setWidthPX(int width) {
        if (getWidgetCount() == 0) {
            return;
        }
        undefinedWidth = (width < 0);

        updateCellWidths();
    }

    public void addEvents(List<CalendarEvent> events) {
        for (CalendarEvent e : events) {
            addEvent(e);
        }
    }

    public void addEvent(CalendarEvent calendarEvent) {
        updateEventSlot(calendarEvent);

        int dateCount = getWidgetCount();
        Date from = calendarEvent.getStart();
        Date to = calendarEvent.getEnd();
        boolean started = false;
        for (int i = 0; i < dateCount; i++) {
            DateCellContainer dc = (DateCellContainer) getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(from);
            int comp2 = dcDate.compareTo(to);
            WeeklyLongEventsDateCell eventLabel = dc.getDateCell(calendarEvent
                    .getSlotIndex());
            eventLabel.setStylePrimaryName("v-calendar-event");
            if (comp >= 0 && comp2 <= 0) {
                eventLabel.setEvent(calendarEvent);
                eventLabel.setCalendar(calendar);

                eventLabel.addStyleDependentName("all-day");
                if (comp == 0) {
                    eventLabel.addStyleDependentName("start");
                }
                if (comp2 == 0) {
                    eventLabel.addStyleDependentName("end");
                }
                if (!started && comp > 0 && comp2 <= 0) {
                    eventLabel.addStyleDependentName("continued-from");
                } else if (i == (dateCount - 1)) {
                    eventLabel.addStyleDependentName("continued-to");
                }
                final String extraStyle = calendarEvent.getStyleName();
                if (extraStyle != null && extraStyle.length() > 0) {
                    eventLabel.addStyleDependentName(extraStyle + "-all-day");
                }
                if (!started) {
                    eventLabel.setText(calendarEvent.getCaption());
                    started = true;
                }
            }
        }
    }

    private void updateEventSlot(CalendarEvent e) {
        boolean foundFreeSlot = false;
        int slot = 0;
        while (!foundFreeSlot) {
            if (isSlotFree(slot, e.getStart(), e.getEnd())) {
                e.setSlotIndex(slot);
                foundFreeSlot = true;

            } else {
                slot++;
            }
        }
    }

    private boolean isSlotFree(int slot, Date start, Date end) {
        int dateCount = getWidgetCount();

        // Go over all dates this week
        for (int i = 0; i < dateCount; i++) {
            DateCellContainer dc = (DateCellContainer) getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(start);
            int comp2 = dcDate.compareTo(end);

            // check if the date is in the range we need
            if (comp >= 0 && comp2 <= 0) {

                // check if the slot is taken
                if (dc.hasEvent(slot)) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void updateCellWidths() {
        int cells = getWidgetCount();
        if (cells <= 0) {
            return;
        }

        int cellWidth = -1;

        // if width is undefined, use the width of the first cell
        // otherwise use distributed sizes
        if (undefinedWidth) {
            cellWidth = calendar.getWeekGrid().getDateCellWidth()
                    - calendar.getWeekGrid().getDateSlotBorder();
        }

        for (int i = 0; i < cells; i++) {
            DateCellContainer dc = (DateCellContainer) getWidget(i);

            if (undefinedWidth) {
                dc.setWidth(cellWidth + "px");

            } else {
                dc.setWidth(calendar.getWeekGrid().getDateCellWidths()[i]
                        + "px");
            }
        }
    }

    @Override
    public String getTooltipKey() {
        return null;
    }
}
