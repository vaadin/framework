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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.calendar.CalendarConnector;
import com.vaadin.client.ui.calendar.schedule.DateCell;
import com.vaadin.client.ui.calendar.schedule.DateCellDayEvent;
import com.vaadin.client.ui.dd.VAcceptCallback;
import com.vaadin.client.ui.dd.VDragEvent;

/**
 * Handles DD when the weekly view is showing in the Calendar. In the weekly
 * view, drops are only allowed in the the time slots for each day. The slot
 * index and the day index are included in the drop details sent to the server.
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 */
public class CalendarWeekDropHandler extends CalendarDropHandler {

    private Element currentTargetElement;
    private DateCell currentTargetDay;

    public CalendarWeekDropHandler(CalendarConnector connector) {
        super(connector);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#dragAccepted
     * (com.vaadin.terminal.gwt.client.ui.dd.VDragEvent)
     */
    @Override
    protected void dragAccepted(VDragEvent drag) {
        deEmphasis();
        currentTargetElement = drag.getElementOver();
        currentTargetDay = WidgetUtil.findWidget(currentTargetElement,
                DateCell.class);
        emphasis();
    }

    /**
     * Removes the CSS style name from the emphasized element
     */
    private void deEmphasis() {
        if (currentTargetElement != null) {
            currentTargetDay.removeEmphasisStyle(currentTargetElement);
            currentTargetElement = null;
        }
    }

    /**
     * Add a CSS stylen name to current target element
     */
    private void emphasis() {
        currentTargetDay.addEmphasisStyle(currentTargetElement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#dragOver(com
     * .vaadin.terminal.gwt.client.ui.dd.VDragEvent)
     */
    @Override
    public void dragOver(final VDragEvent drag) {
        if (isLocationValid(drag.getElementOver())) {
            validate(new VAcceptCallback() {
                @Override
                public void accepted(VDragEvent event) {
                    dragAccepted(drag);
                }
            }, drag);
        }
    }

    /**
     * Checks if the location is a valid drop location
     * 
     * @param elementOver
     *            The element to check
     * @return
     */
    private boolean isLocationValid(Element elementOver) {
        Element weekGridElement = calendarConnector.getWidget().getWeekGrid()
                .getElement();
        Element timeBarElement = calendarConnector.getWidget().getWeekGrid()
                .getTimeBar().getElement();

        Element todayBarElement = null;
        if (calendarConnector.getWidget().getWeekGrid().hasToday()) {
            todayBarElement = calendarConnector.getWidget().getWeekGrid()
                    .getDateCellOfToday().getTodaybarElement();
        }

        // drops are not allowed in:
        // - weekday header
        // - allday event list
        // - todaybar
        // - timebar
        // - events
        return DOM.isOrHasChild(weekGridElement, elementOver)
                && !DOM.isOrHasChild(timeBarElement, elementOver)
                && todayBarElement != elementOver
                && (WidgetUtil.findWidget(elementOver, DateCellDayEvent.class) == null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#dragEnter(com
     * .vaadin.terminal.gwt.client.ui.dd.VDragEvent)
     */
    @Override
    public void dragEnter(VDragEvent drag) {
        // NOOP, we determine drag acceptance in dragOver
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#drop(com.vaadin
     * .terminal.gwt.client.ui.dd.VDragEvent)
     */
    @Override
    public boolean drop(VDragEvent drag) {
        if (isLocationValid(drag.getElementOver())) {
            updateDropDetails(drag);
            deEmphasis();
            return super.drop(drag);

        } else {
            deEmphasis();
            return false;
        }
    }

    /**
     * Update the drop details sent to the server
     * 
     * @param drag
     *            The drag event
     */
    private void updateDropDetails(VDragEvent drag) {
        int slotIndex = currentTargetDay.getSlotIndex(currentTargetElement);
        int dayIndex = calendarConnector.getWidget().getWeekGrid()
                .getDateCellIndex(currentTargetDay);

        drag.getDropDetails().put("dropDayIndex", dayIndex);
        drag.getDropDetails().put("dropSlotIndex", slotIndex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#dragLeave(com
     * .vaadin.terminal.gwt.client.ui.dd.VDragEvent)
     */
    @Override
    public void dragLeave(VDragEvent drag) {
        deEmphasis();
        super.dragLeave(drag);
    }
}
