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

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.ui.VCalendar;

/**
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 * 
 */
public class MonthGrid extends FocusableGrid implements KeyDownHandler {

    private SimpleDayCell selectionStart;
    private SimpleDayCell selectionEnd;
    private final VCalendar calendar;
    private boolean rangeSelectDisabled;
    private boolean enabled = true;
    private final HandlerRegistration keyDownHandler;

    public MonthGrid(VCalendar parent, int rows, int columns) {
        super(rows, columns);
        calendar = parent;
        setCellSpacing(0);
        setCellPadding(0);
        setStylePrimaryName("v-calendar-month");

        keyDownHandler = addKeyDownHandler(this);
    }

    @Override
    protected void onUnload() {
        keyDownHandler.removeHandler();
        super.onUnload();
    }

    public void setSelectionEnd(SimpleDayCell simpleDayCell) {
        selectionEnd = simpleDayCell;
        updateSelection();
    }

    public void setSelectionStart(SimpleDayCell simpleDayCell) {
        if (!rangeSelectDisabled && isEnabled()) {
            selectionStart = simpleDayCell;
            setFocus(true);
        }

    }

    private void updateSelection() {
        if (selectionStart == null) {
            return;
        }
        if (selectionStart != null && selectionEnd != null) {
            Date startDate = selectionStart.getDate();
            Date endDate = selectionEnd.getDate();
            for (int row = 0; row < getRowCount(); row++) {
                for (int cell = 0; cell < getCellCount(row); cell++) {
                    SimpleDayCell sdc = (SimpleDayCell) getWidget(row, cell);
                    if (sdc == null) {
                        return;
                    }
                    Date d = sdc.getDate();
                    if (startDate.compareTo(d) <= 0
                            && endDate.compareTo(d) >= 0) {
                        sdc.addStyleDependentName("selected");
                    } else if (startDate.compareTo(d) >= 0
                            && endDate.compareTo(d) <= 0) {
                        sdc.addStyleDependentName("selected");
                    } else {
                        sdc.removeStyleDependentName("selected");
                    }
                }
            }
        }
    }

    public void setSelectionReady() {
        if (selectionStart != null && selectionEnd != null) {
            String value = "";
            Date startDate = selectionStart.getDate();
            Date endDate = selectionEnd.getDate();
            if (startDate.compareTo(endDate) > 0) {
                Date temp = startDate;
                startDate = endDate;
                endDate = temp;
            }

            if (calendar.getRangeSelectListener() != null) {
                value = calendar.getDateFormat().format(startDate) + "TO"
                        + calendar.getDateFormat().format(endDate);
                calendar.getRangeSelectListener().rangeSelected(value);
            }
            selectionStart = null;
            selectionEnd = null;
            setFocus(false);
        }
    }

    public void cancelRangeSelection() {
        if (selectionStart != null && selectionEnd != null) {
            for (int row = 0; row < getRowCount(); row++) {
                for (int cell = 0; cell < getCellCount(row); cell++) {
                    SimpleDayCell sdc = (SimpleDayCell) getWidget(row, cell);
                    if (sdc == null) {
                        return;
                    }
                    sdc.removeStyleDependentName("selected");
                }
            }
        }
        setFocus(false);
        selectionStart = null;
    }

    public void updateCellSizes(int totalWidthPX, int totalHeightPX) {
        boolean setHeight = totalHeightPX > 0;
        boolean setWidth = totalWidthPX > 0;
        int rows = getRowCount();
        int cells = getCellCount(0);
        int cellWidth = (totalWidthPX / cells) - 1;
        int widthRemainder = totalWidthPX % cells;
        // Division for cells might not be even. Distribute it evenly to
        // will whole space.
        int heightPX = totalHeightPX;
        int cellHeight = heightPX / rows;
        int heightRemainder = heightPX % rows;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cells; j++) {
                SimpleDayCell sdc = (SimpleDayCell) getWidget(i, j);

                if (setWidth) {
                    if (widthRemainder > 0) {
                        sdc.setWidth(cellWidth + 1 + "px");
                        widthRemainder--;

                    } else {
                        sdc.setWidth(cellWidth + "px");
                    }
                }

                if (setHeight) {
                    if (heightRemainder > 0) {
                        sdc.setHeightPX(cellHeight + 1, true);

                    } else {
                        sdc.setHeightPX(cellHeight, true);
                    }
                } else {
                    sdc.setHeightPX(-1, true);
                }
            }
            heightRemainder--;
        }
    }

    /**
     * Disable or enable possibility to select ranges
     */
    public void setRangeSelect(boolean b) {
        rangeSelectDisabled = !b;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        int keycode = event.getNativeKeyCode();
        if (KeyCodes.KEY_ESCAPE == keycode && selectionStart != null) {
            cancelRangeSelection();
        }
    }

    public int getDayCellIndex(SimpleDayCell dayCell) {
        int rows = getRowCount();
        int cells = getCellCount(0);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cells; j++) {
                SimpleDayCell sdc = (SimpleDayCell) getWidget(i, j);
                if (dayCell == sdc) {
                    return i * cells + j;
                }
            }
        }

        return -1;
    }
}
