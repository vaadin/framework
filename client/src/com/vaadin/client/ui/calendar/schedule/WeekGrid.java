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

import java.util.Arrays;
import java.util.Date;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.DateTimeService;
import com.vaadin.client.Util;
import com.vaadin.client.ui.VCalendar;
import com.vaadin.shared.ui.calendar.DateConstants;

/**
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 * 
 */
public class WeekGrid extends SimplePanel {

    int width = 0;
    private int height = 0;
    final HorizontalPanel content;
    private VCalendar calendar;
    private boolean disabled;
    final Timebar timebar;
    private Panel wrapper;
    private boolean verticalScrollEnabled;
    private boolean horizontalScrollEnabled;
    private int[] cellHeights;
    private final int slotInMinutes = 30;
    private int dateCellBorder;
    private DateCell dateCellOfToday;
    private int[] cellWidths;
    private int firstHour;
    private int lastHour;

    public WeekGrid(VCalendar parent, boolean format24h) {
        setCalendar(parent);
        content = new HorizontalPanel();
        timebar = new Timebar(format24h);
        content.add(timebar);

        wrapper = new SimplePanel();
        wrapper.setStylePrimaryName("v-calendar-week-wrapper");
        wrapper.add(content);

        setWidget(wrapper);
    }

    private void setVerticalScroll(boolean isVerticalScrollEnabled) {
        if (isVerticalScrollEnabled && !(isVerticalScrollable())) {
            verticalScrollEnabled = true;
            horizontalScrollEnabled = false;
            wrapper.remove(content);

            final ScrollPanel scrollPanel = new ScrollPanel();
            scrollPanel.setStylePrimaryName("v-calendar-week-wrapper");
            scrollPanel.setWidget(content);

            scrollPanel.addScrollHandler(new ScrollHandler() {
                @Override
                public void onScroll(ScrollEvent event) {
                    if (calendar.getScrollListener() != null) {
                        calendar.getScrollListener().scroll(
                                scrollPanel.getVerticalScrollPosition());
                    }
                }
            });

            setWidget(scrollPanel);
            wrapper = scrollPanel;

        } else if (!isVerticalScrollEnabled && (isVerticalScrollable())) {
            verticalScrollEnabled = false;
            horizontalScrollEnabled = false;
            wrapper.remove(content);

            SimplePanel simplePanel = new SimplePanel();
            simplePanel.setStylePrimaryName("v-calendar-week-wrapper");
            simplePanel.setWidget(content);

            setWidget(simplePanel);
            wrapper = simplePanel;
        }
    }

    public void setVerticalScrollPosition(int verticalScrollPosition) {
        if (isVerticalScrollable()) {
            ((ScrollPanel) wrapper)
                    .setVerticalScrollPosition(verticalScrollPosition);
        }
    }

    public int getInternalWidth() {
        return width;
    }

    public void addDate(Date d) {
        final DateCell dc = new DateCell(this, d);
        dc.setDisabled(isDisabled());
        dc.setHorizontalSized(isHorizontalScrollable() || width < 0);
        dc.setVerticalSized(isVerticalScrollable());
        content.add(dc);
    }

    /**
     * @param dateCell
     * @return get the index of the given date cell in this week, starting from
     *         0
     */
    public int getDateCellIndex(DateCell dateCell) {
        return content.getWidgetIndex(dateCell) - 1;
    }

    /**
     * @return get the slot border in pixels
     */
    public int getDateSlotBorder() {
        return ((DateCell) content.getWidget(1)).getSlotBorder();
    }

    private boolean isVerticalScrollable() {
        return verticalScrollEnabled;
    }

    private boolean isHorizontalScrollable() {
        return horizontalScrollEnabled;
    }

    public void setWidthPX(int width) {
        if (isHorizontalScrollable()) {
            updateCellWidths();

            // Otherwise the scroll wrapper is somehow too narrow = horizontal
            // scroll
            wrapper.setWidth(content.getOffsetWidth()
                    + Util.getNativeScrollbarSize() + "px");

            this.width = content.getOffsetWidth() - timebar.getOffsetWidth();

        } else {
            this.width = (width == -1) ? width : width
                    - timebar.getOffsetWidth();

            if (isVerticalScrollable() && width != -1) {
                this.width = this.width - Util.getNativeScrollbarSize();
            }
            updateCellWidths();
        }
    }

    public void setHeightPX(int intHeight) {
        height = intHeight;

        setVerticalScroll(height <= -1);

        // if not scrollable, use any height given
        if (!isVerticalScrollable() && height > 0) {

            content.setHeight(height + "px");
            setHeight(height + "px");
            wrapper.setHeight(height + "px");
            wrapper.removeStyleDependentName("Vsized");
            updateCellHeights();
            timebar.setCellHeights(cellHeights);
            timebar.setHeightPX(height);

        } else if (isVerticalScrollable()) {
            updateCellHeights();
            wrapper.addStyleDependentName("Vsized");
            timebar.setCellHeights(cellHeights);
            timebar.setHeightPX(height);
        }
    }

    public void clearDates() {
        while (content.getWidgetCount() > 1) {
            content.remove(1);
        }

        dateCellOfToday = null;
    }

    /**
     * @return true if this weekgrid contains a date that is today
     */
    public boolean hasToday() {
        return dateCellOfToday != null;
    }

    public void updateCellWidths() {
        if (!isHorizontalScrollable() && width != -1) {
            int count = content.getWidgetCount();
            int datesWidth = width;
            if (datesWidth > 0 && count > 1) {
                cellWidths = VCalendar
                        .distributeSize(datesWidth, count - 1, -1);

                for (int i = 1; i < count; i++) {
                    DateCell dc = (DateCell) content.getWidget(i);
                    dc.setHorizontalSized(isHorizontalScrollable() || width < 0);
                    dc.setWidthPX(cellWidths[i - 1]);
                    if (dc.isToday()) {
                        dc.setTimeBarWidth(getOffsetWidth());
                    }
                }
            }

        } else {
            int count = content.getWidgetCount();
            if (count > 1) {
                for (int i = 1; i < count; i++) {
                    DateCell dc = (DateCell) content.getWidget(i);
                    dc.setHorizontalSized(isHorizontalScrollable() || width < 0);
                }
            }
        }
    }

    /**
     * @return an int-array containing the widths of the cells (days)
     */
    public int[] getDateCellWidths() {
        return cellWidths;
    }

    public void updateCellHeights() {
        if (!isVerticalScrollable()) {
            int count = content.getWidgetCount();
            if (count > 1) {
                DateCell first = (DateCell) content.getWidget(1);
                dateCellBorder = first.getSlotBorder();
                cellHeights = VCalendar.distributeSize(height,
                        first.getNumberOfSlots(), -dateCellBorder);
                for (int i = 1; i < count; i++) {
                    DateCell dc = (DateCell) content.getWidget(i);
                    dc.setHeightPX(height, cellHeights);
                }
            }

        } else {
            int count = content.getWidgetCount();
            if (count > 1) {
                DateCell first = (DateCell) content.getWidget(1);
                dateCellBorder = first.getSlotBorder();
                int dateHeight = (first.getOffsetHeight() / first
                        .getNumberOfSlots()) - dateCellBorder;
                cellHeights = new int[48];
                Arrays.fill(cellHeights, dateHeight);

                for (int i = 1; i < count; i++) {
                    DateCell dc = (DateCell) content.getWidget(i);
                    dc.setVerticalSized(isVerticalScrollable());
                }
            }
        }
    }

    public void addEvent(CalendarEvent e) {
        int dateCount = content.getWidgetCount();
        Date from = e.getStart();
        Date toTime = e.getEndTime();
        for (int i = 1; i < dateCount; i++) {
            DateCell dc = (DateCell) content.getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(from);
            int comp2 = dcDate.compareTo(toTime);
            if (comp >= 0
                    && comp2 < 0
                    || (comp == 0 && comp2 == 0 && VCalendar
                            .isZeroLengthMidnightEvent(e))) {
                // Same event may be over two DateCells if event's date
                // range floats over one day. It can't float over two days,
                // because event which range is over 24 hours, will be handled
                // as a "fullDay" event.
                dc.addEvent(dcDate, e);
            }
        }
    }

    public int getPixelLengthFor(int startFromMinutes, int durationInMinutes) {
        int pixelLength = 0;
        int currentSlot = 0;

        int firstHourInMinutes = firstHour * DateConstants.HOURINMINUTES;
        int endHourInMinutes = lastHour * DateConstants.HOURINMINUTES;

        if (firstHourInMinutes > startFromMinutes) {
            durationInMinutes = durationInMinutes
                    - (firstHourInMinutes - startFromMinutes);
            startFromMinutes = 0;
        } else {
            startFromMinutes -= firstHourInMinutes;
        }

        int shownHeightInMinutes = endHourInMinutes - firstHourInMinutes
                + DateConstants.HOURINMINUTES;

        durationInMinutes = Math.min(durationInMinutes, shownHeightInMinutes
                - startFromMinutes);

        // calculate full slots to event
        int slotsTillEvent = startFromMinutes / slotInMinutes;
        int startOverFlowTime = slotInMinutes
                - (startFromMinutes % slotInMinutes);
        if (startOverFlowTime == slotInMinutes) {
            startOverFlowTime = 0;
            currentSlot = slotsTillEvent;
        } else {
            currentSlot = slotsTillEvent + 1;
        }

        int durationInSlots = 0;
        int endOverFlowTime = 0;

        if (startOverFlowTime > 0) {
            durationInSlots = (durationInMinutes - startOverFlowTime)
                    / slotInMinutes;
            endOverFlowTime = (durationInMinutes - startOverFlowTime)
                    % slotInMinutes;

        } else {
            durationInSlots = durationInMinutes / slotInMinutes;
            endOverFlowTime = durationInMinutes % slotInMinutes;
        }

        // calculate slot overflow at start
        if (startOverFlowTime > 0 && currentSlot < cellHeights.length) {
            int lastSlotHeight = cellHeights[currentSlot] + dateCellBorder;
            pixelLength += (int) (((double) lastSlotHeight / (double) slotInMinutes) * startOverFlowTime);
        }

        // calculate length in full slots
        int lastFullSlot = currentSlot + durationInSlots;
        for (; currentSlot < lastFullSlot && currentSlot < cellHeights.length; currentSlot++) {
            pixelLength += cellHeights[currentSlot] + dateCellBorder;
        }

        // calculate overflow at end
        if (endOverFlowTime > 0 && currentSlot < cellHeights.length) {
            int lastSlotHeight = cellHeights[currentSlot] + dateCellBorder;
            pixelLength += (int) (((double) lastSlotHeight / (double) slotInMinutes) * endOverFlowTime);
        }

        // reduce possible underflow at end
        if (endOverFlowTime < 0) {
            int lastSlotHeight = cellHeights[currentSlot] + dateCellBorder;
            pixelLength += (int) (((double) lastSlotHeight / (double) slotInMinutes) * endOverFlowTime);
        }

        return pixelLength;
    }

    public int getPixelTopFor(int startFromMinutes) {
        int pixelsToTop = 0;
        int slotIndex = 0;

        int firstHourInMinutes = firstHour * 60;

        if (firstHourInMinutes > startFromMinutes) {
            startFromMinutes = 0;
        } else {
            startFromMinutes -= firstHourInMinutes;
        }

        // calculate full slots to event
        int slotsTillEvent = startFromMinutes / slotInMinutes;
        int overFlowTime = startFromMinutes % slotInMinutes;
        if (slotsTillEvent > 0) {
            for (slotIndex = 0; slotIndex < slotsTillEvent; slotIndex++) {
                pixelsToTop += cellHeights[slotIndex] + dateCellBorder;
            }
        }

        // calculate lengths less than one slot
        if (overFlowTime > 0) {
            int lastSlotHeight = cellHeights[slotIndex] + dateCellBorder;
            pixelsToTop += ((double) lastSlotHeight / (double) slotInMinutes)
                    * overFlowTime;
        }

        return pixelsToTop;
    }

    public void eventMoved(DateCellDayEvent dayEvent) {
        Style s = dayEvent.getElement().getStyle();
        int left = Integer.parseInt(s.getLeft().substring(0,
                s.getLeft().length() - 2));
        DateCell previousParent = (DateCell) dayEvent.getParent();
        DateCell newParent = (DateCell) content
                .getWidget((left / getDateCellWidth()) + 1);
        CalendarEvent se = dayEvent.getCalendarEvent();
        previousParent.removeEvent(dayEvent);
        newParent.addEvent(dayEvent);
        if (!previousParent.equals(newParent)) {
            previousParent.recalculateEventWidths();
        }
        newParent.recalculateEventWidths();
        if (calendar.getEventMovedListener() != null) {
            calendar.getEventMovedListener().eventMoved(se);
        }
    }

    public void setToday(Date todayDate, Date todayTimestamp) {
        int count = content.getWidgetCount();
        if (count > 1) {
            for (int i = 1; i < count; i++) {
                DateCell dc = (DateCell) content.getWidget(i);
                if (dc.getDate().getTime() == todayDate.getTime()) {
                    if (isVerticalScrollable()) {
                        dc.setToday(todayTimestamp, -1);
                    } else {
                        dc.setToday(todayTimestamp, getOffsetWidth());
                    }
                }
                dateCellOfToday = dc;
            }
        }
    }

    public DateCell getDateCellOfToday() {
        return dateCellOfToday;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public Timebar getTimeBar() {
        return timebar;
    }

    public void setDateColor(Date when, Date to, String styleName) {
        int dateCount = content.getWidgetCount();
        for (int i = 1; i < dateCount; i++) {
            DateCell dc = (DateCell) content.getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(when);
            int comp2 = dcDate.compareTo(to);
            if (comp >= 0 && comp2 <= 0) {
                dc.setDateColor(styleName);
            }
        }
    }

    /**
     * @param calendar
     *            the calendar to set
     */
    public void setCalendar(VCalendar calendar) {
        this.calendar = calendar;
    }

    /**
     * @return the calendar
     */
    public VCalendar getCalendar() {
        return calendar;
    }

    /**
     * Get width of the single date cell
     * 
     * @return Date cell width
     */
    public int getDateCellWidth() {
        int count = content.getWidgetCount() - 1;
        int cellWidth = -1;
        if (count <= 0) {
            return cellWidth;
        }

        if (width == -1) {
            Widget firstWidget = content.getWidget(1);
            cellWidth = firstWidget.getElement().getOffsetWidth();
        } else {
            cellWidth = getInternalWidth() / count;
        }
        return cellWidth;
    }

    /**
     * @return the number of day cells in this week
     */
    public int getDateCellCount() {
        return content.getWidgetCount() - 1;
    }

    public void setFirstHour(int firstHour) {
        this.firstHour = firstHour;
        timebar.setFirstHour(firstHour);
    }

    public void setLastHour(int lastHour) {
        this.lastHour = lastHour;
        timebar.setLastHour(lastHour);
    }

    public int getFirstHour() {
        return firstHour;
    }

    public int getLastHour() {
        return lastHour;
    }

    public static class Timebar extends HTML {

        private static final int[] timesFor12h = { 12, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 10, 11 };

        private int height;

        private final int verticalPadding = 7; // FIXME measure this from DOM

        private int[] slotCellHeights;

        private int firstHour;

        private int lastHour;

        public Timebar(boolean format24h) {
            createTimeBar(format24h);
        }

        public void setLastHour(int lastHour) {
            this.lastHour = lastHour;
        }

        public void setFirstHour(int firstHour) {
            this.firstHour = firstHour;

        }

        public void setCellHeights(int[] cellHeights) {
            slotCellHeights = cellHeights;
        }

        private void createTimeBar(boolean format24h) {
            setStylePrimaryName("v-calendar-times");

            // Fist "time" is empty
            Element e = DOM.createDiv();
            setStyleName(e, "v-calendar-time");
            e.setInnerText("");
            getElement().appendChild(e);

            DateTimeService dts = new DateTimeService();

            if (format24h) {
                for (int i = firstHour + 1; i <= lastHour; i++) {
                    e = DOM.createDiv();
                    setStyleName(e, "v-calendar-time");
                    String delimiter = dts.getClockDelimeter();
                    e.setInnerHTML("<span>" + i + "</span>" + delimiter + "00");
                    getElement().appendChild(e);
                }
            } else {
                // FIXME Use dts.getAmPmStrings(); and make sure that
                // DateTimeService has a some Locale set.
                String[] ampm = new String[] { "AM", "PM" };

                int amStop = (lastHour < 11) ? lastHour : 11;
                int pmStart = (firstHour > 11) ? firstHour % 11 : 0;

                if (firstHour < 12) {
                    for (int i = firstHour + 1; i <= amStop; i++) {
                        e = DOM.createDiv();
                        setStyleName(e, "v-calendar-time");
                        e.setInnerHTML("<span>" + timesFor12h[i] + "</span>"
                                + " " + ampm[0]);
                        getElement().appendChild(e);
                    }
                }

                if (lastHour > 11) {
                    for (int i = pmStart; i < lastHour - 11; i++) {
                        e = DOM.createDiv();
                        setStyleName(e, "v-calendar-time");
                        e.setInnerHTML("<span>" + timesFor12h[i] + "</span>"
                                + " " + ampm[1]);
                        getElement().appendChild(e);
                    }
                }
            }
        }

        public void updateTimeBar(boolean format24h) {
            clear();
            createTimeBar(format24h);
        }

        private void clear() {
            while (getElement().getChildCount() > 0) {
                getElement().removeChild(getElement().getChild(0));
            }
        }

        public void setHeightPX(int pixelHeight) {
            height = pixelHeight;

            if (pixelHeight > -1) {
                // as the negative margins on children pulls the whole element
                // upwards, we must compensate. otherwise the element would be
                // too short
                super.setHeight((height + verticalPadding) + "px");
                removeStyleDependentName("Vsized");
                updateChildHeights();

            } else {
                addStyleDependentName("Vsized");
                updateChildHeights();
            }
        }

        private void updateChildHeights() {
            int childCount = getElement().getChildCount();

            if (height != -1) {

                // 23 hours + first is empty
                // we try to adjust the height of time labels to the distributed
                // heights of the time slots
                int hoursPerDay = lastHour - firstHour + 1;

                int slotsPerHour = slotCellHeights.length / hoursPerDay;
                int[] cellHeights = new int[slotCellHeights.length
                        / slotsPerHour];

                int slotHeightPosition = 0;
                for (int i = 0; i < cellHeights.length; i++) {
                    for (int j = slotHeightPosition; j < slotHeightPosition
                            + slotsPerHour; j++) {
                        cellHeights[i] += slotCellHeights[j] + 1;
                        // 1px more for borders
                        // FIXME measure from DOM
                    }
                    slotHeightPosition += slotsPerHour;
                }

                for (int i = 0; i < childCount; i++) {
                    Element e = (Element) getElement().getChild(i);
                    e.getStyle().setHeight(cellHeights[i], Unit.PX);
                }

            } else {
                for (int i = 0; i < childCount; i++) {
                    Element e = (Element) getElement().getChild(i);
                    e.getStyle().setProperty("height", "");
                }
            }
        }
    }

    public VCalendar getParentCalendar() {
        return calendar;
    }
}
