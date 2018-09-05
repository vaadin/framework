/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.client.ui.calendar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.Action;
import com.vaadin.client.ui.ActionOwner;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.v7.client.ui.AbstractLegacyComponentConnector;
import com.vaadin.v7.client.ui.VCalendar;
import com.vaadin.v7.client.ui.VCalendar.BackwardListener;
import com.vaadin.v7.client.ui.VCalendar.DateClickListener;
import com.vaadin.v7.client.ui.VCalendar.EventClickListener;
import com.vaadin.v7.client.ui.VCalendar.EventMovedListener;
import com.vaadin.v7.client.ui.VCalendar.EventResizeListener;
import com.vaadin.v7.client.ui.VCalendar.ForwardListener;
import com.vaadin.v7.client.ui.VCalendar.MouseEventListener;
import com.vaadin.v7.client.ui.VCalendar.RangeSelectListener;
import com.vaadin.v7.client.ui.VCalendar.WeekClickListener;
import com.vaadin.v7.client.ui.calendar.schedule.CalendarDay;
import com.vaadin.v7.client.ui.calendar.schedule.CalendarEvent;
import com.vaadin.v7.client.ui.calendar.schedule.DateCell;
import com.vaadin.v7.client.ui.calendar.schedule.DateCell.DateCellSlot;
import com.vaadin.v7.client.ui.calendar.schedule.DateCellDayEvent;
import com.vaadin.v7.client.ui.calendar.schedule.DateUtil;
import com.vaadin.v7.client.ui.calendar.schedule.HasTooltipKey;
import com.vaadin.v7.client.ui.calendar.schedule.MonthEventLabel;
import com.vaadin.v7.client.ui.calendar.schedule.SimpleDayCell;
import com.vaadin.v7.client.ui.calendar.schedule.dd.CalendarDropHandler;
import com.vaadin.v7.client.ui.calendar.schedule.dd.CalendarMonthDropHandler;
import com.vaadin.v7.client.ui.calendar.schedule.dd.CalendarWeekDropHandler;
import com.vaadin.v7.shared.ui.calendar.CalendarClientRpc;
import com.vaadin.v7.shared.ui.calendar.CalendarEventId;
import com.vaadin.v7.shared.ui.calendar.CalendarServerRpc;
import com.vaadin.v7.shared.ui.calendar.CalendarState;
import com.vaadin.v7.shared.ui.calendar.CalendarState.EventSortOrder;
import com.vaadin.v7.shared.ui.calendar.DateConstants;
import com.vaadin.v7.ui.Calendar;

/**
 * Handles communication between Calendar on the server side and
 * {@link VCalendar} on the client side.
 *
 * @since 7.1
 * @author Vaadin Ltd.
 */
@Connect(value = Calendar.class, loadStyle = LoadStyle.LAZY)
public class CalendarConnector extends AbstractLegacyComponentConnector
        implements ActionOwner, SimpleManagedLayout, Paintable {

    private CalendarServerRpc rpc = RpcProxy.create(CalendarServerRpc.class,
            this);

    private final Map<String, String> actionMap = new HashMap<String, String>();
    private Map<Object, String> tooltips = new HashMap<Object, String>();

    private static final String DROPHANDLER_ACCEPT_CRITERIA_PAINT_TAG = "-ac";

    /**
     *
     */
    public CalendarConnector() {

        // Listen to events
        registerListeners();
    }

    @Override
    protected void init() {
        super.init();
        registerRpc(CalendarClientRpc.class, new CalendarClientRpc() {
            @Override
            public void scroll(int scrollPosition) {
                // TODO widget scroll
            }
        });
        getLayoutManager().registerDependency(this, getWidget().getElement());
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        getLayoutManager().unregisterDependency(this, getWidget().getElement());
    }

    @Override
    public VCalendar getWidget() {
        return (VCalendar) super.getWidget();
    }

    @Override
    public CalendarState getState() {
        return (CalendarState) super.getState();
    }

    /**
     * Registers listeners on the calendar so server can be notified of the
     * events.
     */
    protected void registerListeners() {
        VCalendar calendar = getWidget();
        calendar.setListener(new DateClickListener() {
            @Override
            public void dateClick(String date) {
                if (!calendar.isDisabled()
                        && hasEventListener(CalendarEventId.DATECLICK)) {
                    rpc.dateClick(date);
                }
            }
        });
        calendar.setListener(new ForwardListener() {
            @Override
            public void forward() {
                if (hasEventListener(CalendarEventId.FORWARD)) {
                    rpc.forward();
                }
            }
        });
        calendar.setListener(new BackwardListener() {
            @Override
            public void backward() {
                if (hasEventListener(CalendarEventId.BACKWARD)) {
                    rpc.backward();
                }
            }
        });
        calendar.setListener(new RangeSelectListener() {
            @Override
            public void rangeSelected(String value) {
                if (hasEventListener(CalendarEventId.RANGESELECT)) {
                    rpc.rangeSelect(value);
                }
            }
        });
        calendar.setListener(new WeekClickListener() {
            @Override
            public void weekClick(String event) {
                if (!calendar.isDisabled()
                        && hasEventListener(CalendarEventId.WEEKCLICK)) {
                    rpc.weekClick(event);
                }
            }
        });
        calendar.setListener(new EventMovedListener() {
            @Override
            public void eventMoved(CalendarEvent event) {
                if (hasEventListener(CalendarEventId.EVENTMOVE)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(DateUtil.formatClientSideDate(event.getStart()));
                    sb.append('-');
                    sb.append(DateUtil
                            .formatClientSideTime(event.getStartTime()));
                    rpc.eventMove(event.getIndex(), sb.toString());
                }
            }
        });
        calendar.setListener(new EventResizeListener() {
            @Override
            public void eventResized(CalendarEvent event) {
                if (hasEventListener(CalendarEventId.EVENTRESIZE)) {
                    StringBuilder buffer = new StringBuilder();

                    buffer.append(
                            DateUtil.formatClientSideDate(event.getStart()));
                    buffer.append('-');
                    buffer.append(DateUtil
                            .formatClientSideTime(event.getStartTime()));

                    String newStartDate = buffer.toString();

                    buffer = new StringBuilder();
                    buffer.append(
                            DateUtil.formatClientSideDate(event.getEnd()));
                    buffer.append('-');
                    buffer.append(
                            DateUtil.formatClientSideTime(event.getEndTime()));

                    String newEndDate = buffer.toString();

                    rpc.eventResize(event.getIndex(), newStartDate, newEndDate);
                }
            }
        });
        calendar.setListener(new VCalendar.ScrollListener() {
            @Override
            public void scroll(int scrollPosition) {
                // This call is @Delayed (== non-immediate)
                rpc.scroll(scrollPosition);
            }
        });
        calendar.setListener(new EventClickListener() {
            @Override
            public void eventClick(CalendarEvent event) {
                if (hasEventListener(CalendarEventId.EVENTCLICK)) {
                    rpc.eventClick(event.getIndex());
                }
            }
        });
        calendar.setListener(new MouseEventListener() {
            @Override
            public void contextMenu(ContextMenuEvent event,
                    final Widget widget) {
                final NativeEvent ne = event.getNativeEvent();
                int left = ne.getClientX();
                int top = ne.getClientY();
                top += Window.getScrollTop();
                left += Window.getScrollLeft();
                getClient().getContextMenu().showAt(new ActionOwner() {
                    @Override
                    public String getPaintableId() {
                        return CalendarConnector.this.getPaintableId();
                    }

                    @Override
                    public ApplicationConnection getClient() {
                        return CalendarConnector.this.getClient();
                    }

                    @Override
                    @SuppressWarnings("deprecation")
                    public Action[] getActions() {
                        if (widget instanceof SimpleDayCell) {
                            /*
                             * Month view
                             */
                            SimpleDayCell cell = (SimpleDayCell) widget;
                            Date start = new Date(cell.getDate().getYear(),
                                    cell.getDate().getMonth(),
                                    cell.getDate().getDate(), 0, 0, 0);

                            Date end = new Date(cell.getDate().getYear(),
                                    cell.getDate().getMonth(),
                                    cell.getDate().getDate(), 23, 59, 59);

                            return CalendarConnector.this
                                    .getActionsBetween(start, end);

                        } else if (widget instanceof MonthEventLabel) {
                            MonthEventLabel mel = (MonthEventLabel) widget;
                            CalendarEvent event = mel.getCalendarEvent();
                            Action[] actions = CalendarConnector.this
                                    .getActionsBetween(event.getStartTime(),
                                            event.getEndTime());
                            for (Action action : actions) {
                                ((VCalendarAction) action).setEvent(event);
                            }
                            return actions;

                        } else if (widget instanceof DateCell) {
                            /*
                             * Week and Day view
                             */
                            DateCell cell = (DateCell) widget;
                            int slotIndex = DOM.getChildIndex(cell.getElement(),
                                    (Element) ne.getEventTarget().cast());
                            DateCellSlot slot = cell.getSlot(slotIndex);
                            return CalendarConnector.this.getActionsBetween(
                                    slot.getFrom(), slot.getTo());
                        } else if (widget instanceof DateCellDayEvent) {
                            /*
                             * Context menu on event
                             */
                            DateCellDayEvent dayEvent = (DateCellDayEvent) widget;
                            CalendarEvent event = dayEvent.getCalendarEvent();

                            Action[] actions = CalendarConnector.this
                                    .getActionsBetween(event.getStartTime(),
                                            event.getEndTime());

                            for (Action action : actions) {
                                ((VCalendarAction) action).setEvent(event);
                            }

                            return actions;
                        }
                        return null;
                    }
                }, left, top);
            }
        });
    }

    private boolean showingMonthView() {
        return getState().days.size() > 7;
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        CalendarState state = getState();
        VCalendar calendar = getWidget();

        // Enable or disable the forward and backward navigation buttons
        calendar.setForwardNavigationEnabled(
                hasEventListener(CalendarEventId.FORWARD));
        calendar.setBackwardNavigationEnabled(
                hasEventListener(CalendarEventId.BACKWARD));

        calendar.set24HFormat(state.format24H);
        calendar.setDayNames(state.dayNames);
        calendar.setMonthNames(state.monthNames);
        calendar.setFirstDayNumber(state.firstVisibleDayOfWeek);
        calendar.setLastDayNumber(state.lastVisibleDayOfWeek);
        calendar.setFirstHourOfTheDay(state.firstHourOfDay);
        calendar.setLastHourOfTheDay(state.lastHourOfDay);
        calendar.setReadOnly(state.readOnly);
        calendar.setDisabled(!state.enabled);

        calendar.setRangeSelectAllowed(
                hasEventListener(CalendarEventId.RANGESELECT));
        calendar.setRangeMoveAllowed(
                hasEventListener(CalendarEventId.EVENTMOVE));
        calendar.setEventMoveAllowed(
                hasEventListener(CalendarEventId.EVENTMOVE));
        calendar.setEventResizeAllowed(
                hasEventListener(CalendarEventId.EVENTRESIZE));

        calendar.setEventCaptionAsHtml(state.eventCaptionAsHtml);

        EventSortOrder oldOrder = calendar.getSortOrder();
        if (!SharedUtil.equals(oldOrder, getState().eventSortOrder)) {
            calendar.setSortOrder(getState().eventSortOrder);
        }
        updateEventsInView();

        List<CalendarState.Day> days = state.days;
        List<CalendarState.Event> events = state.events;

        CalendarDropHandler dropHandler = calendar.getDropHandler();
        if (showingMonthView()) {
            updateMonthView(days, events);
            if (dropHandler != null
                    && !(dropHandler instanceof CalendarMonthDropHandler)) {
                calendar.setDropHandler(new CalendarMonthDropHandler(this));
            }
        } else {
            updateWeekView(days, events);
            if (dropHandler != null
                    && !(dropHandler instanceof CalendarWeekDropHandler)) {
                calendar.setDropHandler(new CalendarWeekDropHandler(this));
            }
        }

        updateSizes();

        registerEventToolTips(state.events);
        updateActionMap(state.actions);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.terminal.gwt.client.Paintable#updateFromUIDL(com.vaadin.
     * terminal .gwt.client.UIDL,
     * com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        for (Object c : uidl) {
            UIDL child = (UIDL) c;
            if (DROPHANDLER_ACCEPT_CRITERIA_PAINT_TAG.equals(child.getTag())) {
                if (getWidget().getDropHandler() == null) {
                    getWidget().setDropHandler(showingMonthView()
                            ? new CalendarMonthDropHandler(this)
                            : new CalendarWeekDropHandler(this));
                }
                getWidget().getDropHandler().updateAcceptRules(child);
            } else {
                getWidget().setDropHandler(null);
            }
        }
    }

    /**
     * Returns the ApplicationConnection used to connect to the server side.
     */
    @Override
    public ApplicationConnection getClient() {
        return getConnection();
    }

    /**
     * Register the description of the events as tooltips. This way, any event
     * displaying widget can use the event index as a key to display the
     * tooltip.
     */
    private void registerEventToolTips(List<CalendarState.Event> events) {
        for (CalendarState.Event e : events) {
            if (e.description != null && !"".equals(e.description)) {
                tooltips.put(e.index, e.description);
            } else {
                tooltips.remove(e.index);
            }
        }
    }

    @Override
    public TooltipInfo getTooltipInfo(
            com.google.gwt.dom.client.Element element) {
        TooltipInfo tooltipInfo = null;
        Widget w = WidgetUtil.findWidget(element, null);
        if (w instanceof HasTooltipKey) {
            tooltipInfo = GWT.create(TooltipInfo.class);
            String title = tooltips.get(((HasTooltipKey) w).getTooltipKey());
            tooltipInfo.setTitle(title != null ? title : "");
        }
        if (tooltipInfo == null) {
            tooltipInfo = super.getTooltipInfo(element);
        }
        return tooltipInfo;
    }

    @Override
    public boolean hasTooltip() {
        /*
         * Tooltips are not processed until updateFromUIDL, so we can't be sure
         * that there are no tooltips during onStateChange when this is used.
         */
        return true;
    }

    private void updateEventsInView() {
        CalendarState state = getState();
        List<CalendarState.Day> days = state.days;
        List<CalendarState.Event> events = state.events;

        CalendarDropHandler dropHandler = getWidget().getDropHandler();
        if (showingMonthView()) {
            updateMonthView(days, events);
            if (dropHandler != null
                    && !(dropHandler instanceof CalendarMonthDropHandler)) {
                getWidget().setDropHandler(new CalendarMonthDropHandler(this));
            }
        } else {
            updateWeekView(days, events);
            if (dropHandler != null
                    && !(dropHandler instanceof CalendarWeekDropHandler)) {
                getWidget().setDropHandler(new CalendarWeekDropHandler(this));
            }
        }
    }

    private void updateMonthView(List<CalendarState.Day> days,
            List<CalendarState.Event> events) {
        CalendarState state = getState();
        getWidget().updateMonthView(state.firstDayOfWeek,
                getWidget().getDateTimeFormat().parse(state.now), days.size(),
                calendarEventListOf(events, state.format24H),
                calendarDayListOf(days));
    }

    private void updateWeekView(List<CalendarState.Day> days,
            List<CalendarState.Event> events) {
        CalendarState state = getState();
        getWidget().updateWeekView(state.scroll,
                getWidget().getDateTimeFormat().parse(state.now), days.size(),
                state.firstDayOfWeek,
                calendarEventListOf(events, state.format24H),
                calendarDayListOf(days));
    }

    private Action[] getActionsBetween(Date start, Date end) {
        List<Action> actions = new ArrayList<Action>();
        List<String> ids = new ArrayList<String>();

        for (int i = 0; i < actionKeys.size(); i++) {
            String actionKey = actionKeys.get(i);
            String id = getActionID(actionKey);
            if (!ids.contains(id)) {

                Date actionStartDate;
                Date actionEndDate;
                try {
                    actionStartDate = getActionStartDate(actionKey);
                    actionEndDate = getActionEndDate(actionKey);
                } catch (ParseException pe) {
                    getLogger().severe("Failed to parse action date");
                    continue;
                }

                // Case 0: action inside event timeframe
                // Action should start AFTER or AT THE SAME TIME as the event,
                // and
                // Action should end BEFORE or AT THE SAME TIME as the event
                boolean test0 = actionStartDate.compareTo(start) >= 0
                        && actionEndDate.compareTo(end) <= 0;

                // Case 1: action intersects start of timeframe
                // Action end time must be between start and end of event
                boolean test1 = actionEndDate.compareTo(start) > 0
                        && actionEndDate.compareTo(end) <= 0;

                // Case 2: action intersects end of timeframe
                // Action start time must be between start and end of event
                boolean test2 = actionStartDate.compareTo(start) >= 0
                        && actionStartDate.compareTo(end) < 0;

                // Case 3: event inside action timeframe
                // Action should start AND END before the event is complete
                boolean test3 = start.compareTo(actionStartDate) >= 0
                        && end.compareTo(actionEndDate) <= 0;

                if (test0 || test1 || test2 || test3) {
                    VCalendarAction a = new VCalendarAction(this, rpc,
                            actionKey);
                    a.setCaption(getActionCaption(actionKey));
                    a.setIconUrl(getActionIcon(actionKey));
                    a.setActionStartDate(start);
                    a.setActionEndDate(end);
                    actions.add(a);
                    ids.add(id);
                }
            }
        }

        return actions.toArray(new Action[actions.size()]);
    }

    private List<String> actionKeys = new ArrayList<String>();

    private void updateActionMap(List<CalendarState.Action> actions) {
        actionMap.clear();
        actionKeys.clear();

        if (actions == null) {
            return;
        }

        for (CalendarState.Action action : actions) {
            String id = action.actionKey + "-" + action.startDate + "-"
                    + action.endDate;
            actionMap.put(id + "_k", action.actionKey);
            actionMap.put(id + "_c", action.caption);
            actionMap.put(id + "_s", action.startDate);
            actionMap.put(id + "_e", action.endDate);
            actionKeys.add(id);
            if (action.iconKey != null) {
                actionMap.put(id + "_i", getResourceUrl(action.iconKey));

            } else {
                actionMap.remove(id + "_i");
            }
        }

        Collections.sort(actionKeys);
    }

    /**
     * Get the original action ID that was passed in from the shared state.
     *
     * @since 7.1.2
     * @param actionKey
     *            the unique action key
     * @return
     */
    public String getActionID(String actionKey) {
        return actionMap.get(actionKey + "_k");
    }

    /**
     * Get the text that is displayed for a context menu item.
     *
     * @param actionKey
     *            The unique action key
     * @return
     */
    public String getActionCaption(String actionKey) {
        return actionMap.get(actionKey + "_c");
    }

    /**
     * Get the icon url for a context menu item.
     *
     * @param actionKey
     *            The unique action key
     * @return
     */
    public String getActionIcon(String actionKey) {
        return actionMap.get(actionKey + "_i");
    }

    /**
     * Get the start date for an action item.
     *
     * @param actionKey
     *            The unique action key
     * @return
     * @throws ParseException
     */
    public Date getActionStartDate(String actionKey) throws ParseException {
        String dateStr = actionMap.get(actionKey + "_s");
        DateTimeFormat formatter = DateTimeFormat
                .getFormat(DateConstants.ACTION_DATE_FORMAT_PATTERN);
        return formatter.parse(dateStr);
    }

    /**
     * Get the end date for an action item.
     *
     * @param actionKey
     *            The unique action key
     * @return
     * @throws ParseException
     */
    public Date getActionEndDate(String actionKey) throws ParseException {
        String dateStr = actionMap.get(actionKey + "_e");
        DateTimeFormat formatter = DateTimeFormat
                .getFormat(DateConstants.ACTION_DATE_FORMAT_PATTERN);
        return formatter.parse(dateStr);
    }

    /**
     * Returns ALL currently registered events. Use {@link #getActions(Date)} to
     * get the actions for a specific date
     */
    @Override
    public Action[] getActions() {
        List<Action> actions = new ArrayList<Action>();
        for (int i = 0; i < actionKeys.size(); i++) {
            final String actionKey = actionKeys.get(i);
            final VCalendarAction a = new VCalendarAction(this, rpc, actionKey);
            a.setCaption(getActionCaption(actionKey));
            a.setIconUrl(getActionIcon(actionKey));

            try {
                a.setActionStartDate(getActionStartDate(actionKey));
                a.setActionEndDate(getActionEndDate(actionKey));
            } catch (ParseException pe) {
                getLogger().log(Level.SEVERE,
                        pe.getMessage() == null ? "" : pe.getMessage(), pe);
            }

            actions.add(a);
        }
        return actions.toArray(new Action[actions.size()]);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.terminal.gwt.client.ui.ActionOwner#getPaintableId()
     */
    @Override
    public String getPaintableId() {
        return getConnectorId();
    }

    private List<CalendarEvent> calendarEventListOf(
            List<CalendarState.Event> events, boolean format24h) {
        List<CalendarEvent> list = new ArrayList<CalendarEvent>(events.size());
        for (CalendarState.Event event : events) {
            final String dateFrom = event.dateFrom;
            final String dateTo = event.dateTo;
            final String timeFrom = event.timeFrom;
            final String timeTo = event.timeTo;
            CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setAllDay(event.allDay);
            calendarEvent.setCaption(event.caption);
            calendarEvent.setDescription(event.description);
            calendarEvent.setStart(getWidget().getDateFormat().parse(dateFrom));
            calendarEvent.setEnd(getWidget().getDateFormat().parse(dateTo));
            calendarEvent.setFormat24h(format24h);
            calendarEvent.setStartTime(getWidget().getDateTimeFormat()
                    .parse(dateFrom + " " + timeFrom));
            calendarEvent.setEndTime(getWidget().getDateTimeFormat()
                    .parse(dateTo + " " + timeTo));
            calendarEvent.setStyleName(event.styleName);
            calendarEvent.setIndex(event.index);
            list.add(calendarEvent);
        }
        return list;
    }

    private List<CalendarDay> calendarDayListOf(List<CalendarState.Day> days) {
        List<CalendarDay> list = new ArrayList<CalendarDay>(days.size());
        for (CalendarState.Day day : days) {
            CalendarDay d = new CalendarDay(day.date, day.localizedDateFormat,
                    day.dayOfWeek, day.week, day.yearOfWeek);

            list.add(d);
        }
        return list;
    }

    @Override
    public void layout() {
        updateSizes();
    }

    private void updateSizes() {
        int height = getLayoutManager()
                .getOuterHeight(getWidget().getElement());
        int width = getLayoutManager().getOuterWidth(getWidget().getElement());

        if (isUndefinedWidth()) {
            width = -1;
        }
        if (isUndefinedHeight()) {
            height = -1;
        }

        getWidget().setSizeForChildren(width, height);

    }

    private static Logger getLogger() {
        return Logger.getLogger(CalendarConnector.class.getName());
    }
}
