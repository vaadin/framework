/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.ui;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.DateTimeService;
import com.vaadin.shared.ui.datefield.DateTimeResolution;

/**
 * @author Vaadin Ltd
 * @since 8.0
 */
public class VDateTimeCalendarPanel
        extends VAbstractCalendarPanel<DateTimeResolution> {

    private static final String SUBPART_HOUR_SELECT = "h";
    private static final String SUBPART_MINUTE_SELECT = "m";
    private static final String SUBPART_SECS_SELECT = "s";
    private static final String SUBPART_AMPM_SELECT = "ampm";

    private TimeChangeListener timeChangeListener;

    private VTime time;

    /**
     * TimeSelector is a widget consisting of list boxes that modifie the Date
     * object that is given for.
     *
     */
    public class VTime extends FlowPanel implements ChangeHandler {

        private ListBox hours;

        private ListBox mins;

        private ListBox sec;

        private ListBox ampm;

        /**
         * Constructor
         */
        public VTime() {
            super();
            setStyleName(VDateField.CLASSNAME + "-time");
            buildTime();
        }

        private ListBox createListBox() {
            ListBox lb = new ListBox();
            lb.setStyleName("v-select");
            lb.addChangeHandler(this);
            lb.addBlurHandler(VDateTimeCalendarPanel.this);
            lb.addFocusHandler(VDateTimeCalendarPanel.this);
            return lb;
        }

        /**
         * Constructs the ListBoxes and updates their value
         *
         * @param redraw
         *            Should new instances of the listboxes be created
         */
        private void buildTime() {
            clear();

            hours = createListBox();
            if (getDateTimeService().isTwelveHourClock()) {
                hours.addItem("12");
                for (int i = 1; i < 12; i++) {
                    hours.addItem((i < 10) ? "0" + i : "" + i);
                }
            } else {
                for (int i = 0; i < 24; i++) {
                    hours.addItem((i < 10) ? "0" + i : "" + i);
                }
            }

            hours.addChangeHandler(this);
            if (getDateTimeService().isTwelveHourClock()) {
                ampm = createListBox();
                final String[] ampmText = getDateTimeService().getAmPmStrings();
                ampm.addItem(ampmText[0]);
                ampm.addItem(ampmText[1]);
                ampm.addChangeHandler(this);
            }

            if (getResolution().compareTo(DateTimeResolution.MINUTE) <= 0) {
                mins = createListBox();
                for (int i = 0; i < 60; i++) {
                    mins.addItem((i < 10) ? "0" + i : "" + i);
                }
                mins.addChangeHandler(this);
            }
            if (getResolution().compareTo(DateTimeResolution.SECOND) <= 0) {
                sec = createListBox();
                for (int i = 0; i < 60; i++) {
                    sec.addItem((i < 10) ? "0" + i : "" + i);
                }
                sec.addChangeHandler(this);
            }

            final String delimiter = getDateTimeService().getClockDelimeter();
            if (isReadonly()) {
                int h = 0;
                if (getDate() != null) {
                    h = getDate().getHours();
                }
                if (getDateTimeService().isTwelveHourClock()) {
                    h -= h < 12 ? 0 : 12;
                }
                add(new VLabel(h < 10 ? "0" + h : "" + h));
            } else {
                add(hours);
            }

            if (getResolution().compareTo(DateTimeResolution.MINUTE) <= 0) {
                add(new VLabel(delimiter));
                if (isReadonly()) {
                    final int m = mins.getSelectedIndex();
                    add(new VLabel(m < 10 ? "0" + m : "" + m));
                } else {
                    add(mins);
                }
            }
            if (getResolution().compareTo(DateTimeResolution.SECOND) <= 0) {
                add(new VLabel(delimiter));
                if (isReadonly()) {
                    final int s = sec.getSelectedIndex();
                    add(new VLabel(s < 10 ? "0" + s : "" + s));
                } else {
                    add(sec);
                }
            }
            if (getResolution() == DateTimeResolution.HOUR) {
                add(new VLabel(delimiter + "00")); // o'clock
            }
            if (getDateTimeService().isTwelveHourClock()) {
                add(new VLabel("&nbsp;"));
                if (isReadonly()) {
                    int i = 0;
                    if (getDate() != null) {
                        i = (getDate().getHours() < 12) ? 0 : 1;
                    }
                    add(new VLabel(ampm.getItemText(i)));
                } else {
                    add(ampm);
                }
            }

            if (isReadonly()) {
                return;
            }

            // Update times
            updateTimes();

            ListBox lastDropDown = getLastDropDown();
            lastDropDown.addKeyDownHandler(new KeyDownHandler() {
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    boolean shiftKey = event.getNativeEvent().getShiftKey();
                    if (shiftKey) {
                        return;
                    } else {
                        int nativeKeyCode = event.getNativeKeyCode();
                        if (nativeKeyCode == KeyCodes.KEY_TAB) {
                            onTabOut(event);
                        }
                    }
                }
            });

        }

        private ListBox getLastDropDown() {
            int i = getWidgetCount() - 1;
            while (i >= 0) {
                Widget widget = getWidget(i);
                if (widget instanceof ListBox) {
                    return (ListBox) widget;
                }
                i--;
            }
            return null;
        }

        /**
         * Updates the valus to correspond to the values in value
         */
        public void updateTimes() {
            if (getDate() == null) {
                setDate(new Date());
            }
            if (getDateTimeService().isTwelveHourClock()) {
                int h = getDate().getHours();
                ampm.setSelectedIndex(h < 12 ? 0 : 1);
                h -= ampm.getSelectedIndex() * 12;
                hours.setSelectedIndex(h);
            } else {
                hours.setSelectedIndex(getDate().getHours());
            }
            if (getResolution().compareTo(DateTimeResolution.MINUTE) <= 0) {
                mins.setSelectedIndex(getDate().getMinutes());
            }
            if (getResolution().compareTo(DateTimeResolution.SECOND) <= 0) {
                sec.setSelectedIndex(getDate().getSeconds());
            }
            if (getDateTimeService().isTwelveHourClock()) {
                ampm.setSelectedIndex(getDate().getHours() < 12 ? 0 : 1);
            }

            hours.setEnabled(isEnabled());
            if (mins != null) {
                mins.setEnabled(isEnabled());
            }
            if (sec != null) {
                sec.setEnabled(isEnabled());
            }
            if (ampm != null) {
                ampm.setEnabled(isEnabled());
            }

        }

        private DateTimeService getDateTimeService() {
            if (VDateTimeCalendarPanel.this.getDateTimeService() == null) {
                setDateTimeService(new DateTimeService());
            }
            return VDateTimeCalendarPanel.this.getDateTimeService();
        }

        /*
         * (non-Javadoc) VT
         *
         * @see
         * com.google.gwt.event.dom.client.ChangeHandler#onChange(com.google.gwt
         * .event.dom.client.ChangeEvent)
         */
        @Override
        public void onChange(ChangeEvent event) {
            /*
             * Value from dropdowns gets always set for the value. Like year and
             * month when resolution is month or year.
             */
            if (event.getSource() == hours) {
                int h = hours.getSelectedIndex();
                if (getDateTimeService().isTwelveHourClock()) {
                    h = h + ampm.getSelectedIndex() * 12;
                }
                getDate().setHours(h);
                if (timeChangeListener != null) {
                    timeChangeListener.changed(h, getDate().getMinutes(),
                            getDate().getSeconds(),
                            DateTimeService.getMilliseconds(getDate()));
                }
                event.preventDefault();
                event.stopPropagation();
            } else if (event.getSource() == mins) {
                final int m = mins.getSelectedIndex();
                getDate().setMinutes(m);
                if (timeChangeListener != null) {
                    timeChangeListener.changed(getDate().getHours(), m,
                            getDate().getSeconds(),
                            DateTimeService.getMilliseconds(getDate()));
                }
                event.preventDefault();
                event.stopPropagation();
            } else if (event.getSource() == sec) {
                final int s = sec.getSelectedIndex();
                getDate().setSeconds(s);
                if (timeChangeListener != null) {
                    timeChangeListener.changed(getDate().getHours(),
                            getDate().getMinutes(), s,
                            DateTimeService.getMilliseconds(getDate()));
                }
                event.preventDefault();
                event.stopPropagation();
            } else if (event.getSource() == ampm) {
                final int h = hours.getSelectedIndex()
                        + (ampm.getSelectedIndex() * 12);
                getDate().setHours(h);
                if (timeChangeListener != null) {
                    timeChangeListener.changed(h, getDate().getMinutes(),
                            getDate().getSeconds(),
                            DateTimeService.getMilliseconds(getDate()));
                }
                event.preventDefault();
                event.stopPropagation();
            }
        }

    }

    /**
     * Dispatches an event when the panel when time is changed
     */
    public interface TimeChangeListener {

        void changed(int hour, int min, int sec, int msec);
    }

    /**
     * The time change listener is triggered when the user changes the time.
     *
     * @param listener
     */
    public void setTimeChangeListener(TimeChangeListener listener) {
        timeChangeListener = listener;
    }

    @Override
    public void setDate(Date currentDate) {
        doSetDate(currentDate, isTimeSelectorNeeded() && time == null, () -> {
            if (isTimeSelectorNeeded()) {
                time.updateTimes();
            }
        });
    }

    @Override
    public void setResolution(DateTimeResolution resolution) {
        super.setResolution(resolution);
        if (isTimeSelectorNeeded() && time != null) {
            // resolution has changed => rebuild time UI
            time.buildTime();
        }
    }

    @Override
    protected boolean acceptDayFocus() {
        return getResolution().compareTo(DateTimeResolution.MONTH) < 0;
    }

    @Override
    protected boolean isDay(DateTimeResolution resolution) {
        return DateTimeResolution.DAY.equals(resolution);
    }

    @Override
    protected boolean isMonth(DateTimeResolution resolution) {
        return DateTimeResolution.MONTH.equals(resolution);
    }

    @Override
    protected boolean isBelowMonth(DateTimeResolution resolution) {
        return resolution.compareTo(DateTimeResolution.MONTH) < 0;
    }

    @Override
    protected void doRenderCalendar(boolean updateDate) {
        super.doRenderCalendar(updateDate);

        if (isTimeSelectorNeeded()) {
            time = new VTime();
            setWidget(2, 0, time);
            getFlexCellFormatter().setColSpan(2, 0, 5);
            getFlexCellFormatter().setStyleName(2, 0,
                    getDateField().getStylePrimaryName()
                            + "-calendarpanel-time");
        } else if (time != null) {
            remove(time);
        }
    }

    @Override
    public String getSubPartName(Element subElement) {
        if (time != null) {
            if (contains(time.hours, subElement)) {
                return SUBPART_HOUR_SELECT;
            } else if (contains(time.mins, subElement)) {
                return SUBPART_MINUTE_SELECT;
            } else if (contains(time.sec, subElement)) {
                return SUBPART_SECS_SELECT;
            } else if (contains(time.ampm, subElement)) {
                return SUBPART_AMPM_SELECT;

            }
        }
        return super.getSubPartName(subElement);
    }

    @Override
    public Element getSubPartElement(String subPart) {
        if (SUBPART_HOUR_SELECT.equals(subPart)) {
            return time.hours.getElement();
        }
        if (SUBPART_MINUTE_SELECT.equals(subPart)) {
            return time.mins.getElement();
        }
        if (SUBPART_SECS_SELECT.equals(subPart)) {
            return time.sec.getElement();
        }
        if (SUBPART_AMPM_SELECT.equals(subPart)) {
            return time.ampm.getElement();
        }
        return super.getSubPartElement(subPart);
    }

    /**
     * Do we need the time selector
     *
     * @return True if it is required
     */
    private boolean isTimeSelectorNeeded() {
        return getResolution().compareTo(DateTimeResolution.DAY) < 0;
    }
}
