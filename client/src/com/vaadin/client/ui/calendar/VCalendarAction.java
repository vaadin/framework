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
package com.vaadin.client.ui.calendar;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.vaadin.client.ui.Action;
import com.vaadin.client.ui.calendar.schedule.CalendarEvent;
import com.vaadin.shared.ui.calendar.CalendarServerRpc;
import com.vaadin.shared.ui.calendar.DateConstants;

/**
 * Action performed by the calendar
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 */
public class VCalendarAction extends Action {

    private CalendarServerRpc rpc;

    private String actionKey = "";

    private Date actionStartDate;

    private Date actionEndDate;

    private CalendarEvent event;

    private final DateTimeFormat dateformat_datetime = DateTimeFormat
            .getFormat(DateConstants.ACTION_DATE_FORMAT_PATTERN);

    /**
     * 
     * @param owner
     */
    public VCalendarAction(CalendarConnector owner) {
        super(owner);
    }

    /**
     * Constructor
     * 
     * @param owner
     *            The owner who trigger this kinds of events
     * @param rpc
     *            The CalendarRpc which is used for executing actions
     * @param key
     *            The unique action key which identifies this particular action
     */
    public VCalendarAction(CalendarConnector owner, CalendarServerRpc rpc,
            String key) {
        this(owner);
        this.rpc = rpc;
        actionKey = key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ui.Action#execute()
     */
    @Override
    public void execute() {
        String startDate = dateformat_datetime.format(actionStartDate);
        String endDate = dateformat_datetime.format(actionEndDate);

        if (event == null) {
            rpc.actionOnEmptyCell(actionKey.split("-")[0], startDate, endDate);
        } else {
            rpc.actionOnEvent(actionKey.split("-")[0], startDate, endDate,
                    event.getIndex());
        }

        owner.getClient().getContextMenu().hide();
    }

    /**
     * Get the date and time when the action starts
     * 
     * @return
     */
    public Date getActionStartDate() {
        return actionStartDate;
    }

    /**
     * Set the date when the actions start
     * 
     * @param actionStartDate
     *            The date and time when the action starts
     */
    public void setActionStartDate(Date actionStartDate) {
        this.actionStartDate = actionStartDate;
    }

    /**
     * Get the date and time when the action ends
     * 
     * @return
     */
    public Date getActionEndDate() {
        return actionEndDate;
    }

    /**
     * Set the date and time when the action ends
     * 
     * @param actionEndDate
     *            The date and time when the action ends
     */
    public void setActionEndDate(Date actionEndDate) {
        this.actionEndDate = actionEndDate;
    }

    public CalendarEvent getEvent() {
        return event;
    }

    public void setEvent(CalendarEvent event) {
        this.event = event;
    }

}
