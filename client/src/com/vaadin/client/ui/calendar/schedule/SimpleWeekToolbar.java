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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.vaadin.client.ui.VCalendar;

/**
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 * 
 */
public class SimpleWeekToolbar extends FlexTable implements ClickHandler {
    private int height;
    private VCalendar calendar;
    private boolean isHeightUndefined;

    public SimpleWeekToolbar(VCalendar parent) {
        calendar = parent;
        setCellSpacing(0);
        setCellPadding(0);
        setStyleName("v-calendar-week-numbers");
    }

    public void addWeek(int week, int year) {
        WeekLabel l = new WeekLabel(week + "", week, year);
        l.addClickHandler(this);
        int rowCount = getRowCount();
        insertRow(rowCount);
        setWidget(rowCount, 0, l);
        updateCellHeights();
    }

    public void updateCellHeights() {
        if (!isHeightUndefined()) {
            int rowCount = getRowCount();
            if (rowCount == 0) {
                return;
            }
            int cellheight = (height / rowCount) - 1;
            int remainder = height % rowCount;
            if (cellheight < 0) {
                cellheight = 0;
            }
            for (int i = 0; i < rowCount; i++) {
                if (remainder > 0) {
                    getWidget(i, 0).setHeight(cellheight + 1 + "px");
                } else {
                    getWidget(i, 0).setHeight(cellheight + "px");
                }
                getWidget(i, 0).getElement().getStyle()
                        .setProperty("lineHeight", cellheight + "px");
                remainder--;
            }
        } else {
            for (int i = 0; i < getRowCount(); i++) {
                getWidget(i, 0).setHeight("");
                getWidget(i, 0).getElement().getStyle()
                        .setProperty("lineHeight", "");
            }
        }
    }

    public void setHeightPX(int intHeight) {
        setHeightUndefined(intHeight == -1);
        height = intHeight;
        updateCellHeights();
    }

    public boolean isHeightUndefined() {
        return isHeightUndefined;
    }

    public void setHeightUndefined(boolean isHeightUndefined) {
        this.isHeightUndefined = isHeightUndefined;

        if (isHeightUndefined) {
            addStyleDependentName("Vsized");

        } else {
            removeStyleDependentName("Vsized");
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        WeekLabel wl = (WeekLabel) event.getSource();
        if (calendar.getWeekClickListener() != null) {
            calendar.getWeekClickListener().weekClick(
                    wl.getYear() + "w" + wl.getWeek());
        }
    }
}
