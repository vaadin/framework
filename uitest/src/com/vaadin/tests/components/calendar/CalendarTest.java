/**
 * Copyright 2013 Vaadin Ltd.
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

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Calendar.TimeFormat;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.DateClickEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.WeekClickHandler;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.ui.components.calendar.handler.BasicWeekClickHandler;

/** Calendar component test application */
@Theme("tests-calendar")
public class CalendarTest extends UI {

    private static final long serialVersionUID = -5436777475398410597L;

    private static final String DEFAULT_ITEMID = "DEFAULT";

    private enum Mode {
        MONTH, WEEK, DAY;
    }

    /**
     * This Gregorian calendar is used to control dates and time inside of this
     * test application.
     */
    private GregorianCalendar calendar;

    /** Target calendar component that this test application is made for. */
    private Calendar calendarComponent;

    private Date currentMonthsFirstDate;

    private final Label captionLabel = new Label("");

    private Button monthButton;

    private Button weekButton;

    private Button nextButton;

    private Button prevButton;

    private ComboBox timeZoneSelect;

    private ComboBox formatSelect;

    private ComboBox localeSelect;

    private CheckBox hideWeekendsButton;

    private CheckBox readOnlyButton;

    private TextField captionField;

    private Window scheduleEventPopup;

    private final FormLayout scheduleEventFieldLayout = new FormLayout();
    private FieldGroup scheduleEventFieldGroup = new FieldGroup();

    private Button deleteEventButton;

    private Button applyEventButton;

    private Mode viewMode = Mode.MONTH;

    private BasicEventProvider dataSource;

    private Button addNewEvent;

    /*
     * When testBench is set to true, CalendarTest will have static content that
     * is more suitable for Vaadin TestBench testing. Calendar will use a static
     * date Mon 10 Jan 2000. Enable by starting the application with a
     * "testBench" parameter in the URL.
     */
    private boolean testBench = false;

    private String calendarHeight = null;

    private String calendarWidth = null;

    private CheckBox disabledButton;

    private Integer firstHour;

    private Integer lastHour;

    private Integer firstDay;

    private Integer lastDay;

    private Locale defaultLocale = Locale.US;

    private boolean showWeeklyView;

    private boolean useSecondResolution;

    private DateField startDateField;
    private DateField endDateField;

    @SuppressWarnings("serial")
    @Override
    public void init(VaadinRequest request) {
        GridLayout layout = new GridLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        setContent(layout);

        handleURLParams(request.getParameterMap());

        initContent();
    }

    private void handleURLParams(Map<String, String[]> parameters) {
        testBench = parameters.containsKey("testBench")
                || parameters.containsKey("?testBench");

        if (parameters.containsKey("width")) {
            calendarWidth = parameters.get("width")[0];
        }

        if (parameters.containsKey("height")) {
            calendarHeight = parameters.get("height")[0];
        }

        if (parameters.containsKey("firstDay")) {
            firstDay = Integer.parseInt(parameters.get("firstDay")[0]);
        }

        if (parameters.containsKey("lastDay")) {
            lastDay = Integer.parseInt(parameters.get("lastDay")[0]);
        }

        if (parameters.containsKey("firstHour")) {
            firstHour = Integer.parseInt(parameters.get("firstHour")[0]);
        }

        if (parameters.containsKey("lastHour")) {
            lastHour = Integer.parseInt(parameters.get("lastHour")[0]);
        }

        if (parameters.containsKey("locale")) {
            String localeArray[] = parameters.get("locale")[0].split("_");
            defaultLocale = new Locale(localeArray[0], localeArray[1]);
            setLocale(defaultLocale);
        }

        if (parameters.containsKey(("secondsResolution"))) {
            useSecondResolution = true;
        }

        showWeeklyView = parameters.containsKey("weekly");

    }

    public void initContent() {
        // Set default Locale for this application
        if (testBench) {
            setLocale(defaultLocale);

        } else {
            setLocale(Locale.getDefault());
        }

        // Initialize locale, timezone and timeformat selects.
        localeSelect = createLocaleSelect();
        timeZoneSelect = createTimeZoneSelect();
        formatSelect = createCalendarFormatSelect();

        initCalendar();
        initLayoutContent();
        addInitialEvents();
    }

    private Date resolveFirstDateOfWeek(Date today,
            java.util.Calendar currentCalendar) {
        int firstDayOfWeek = currentCalendar.getFirstDayOfWeek();
        currentCalendar.setTime(today);
        while (firstDayOfWeek != currentCalendar
                .get(java.util.Calendar.DAY_OF_WEEK)) {
            currentCalendar.add(java.util.Calendar.DATE, -1);
        }
        return currentCalendar.getTime();
    }

    private Date resolveLastDateOfWeek(Date today,
            java.util.Calendar currentCalendar) {
        currentCalendar.setTime(today);
        currentCalendar.add(java.util.Calendar.DATE, 1);
        int firstDayOfWeek = currentCalendar.getFirstDayOfWeek();
        // Roll to weeks last day using firstdayofweek. Roll until FDofW is
        // found and then roll back one day.
        while (firstDayOfWeek != currentCalendar
                .get(java.util.Calendar.DAY_OF_WEEK)) {
            currentCalendar.add(java.util.Calendar.DATE, 1);
        }
        currentCalendar.add(java.util.Calendar.DATE, -1);
        return currentCalendar.getTime();
    }

    private void addInitialEvents() {
        Date originalDate = calendar.getTime();
        Date today = getToday();

        // Add a event that last a whole week

        Date start = resolveFirstDateOfWeek(today, calendar);
        Date end = resolveLastDateOfWeek(today, calendar);
        CalendarTestEvent event = getNewEvent("Whole week event", start, end);
        event.setAllDay(true);
        event.setStyleName("color4");
        event.setDescription("Description for the whole week event.");
        dataSource.addEvent(event);

        // Add a allday event
        calendar.setTime(start);
        calendar.add(GregorianCalendar.DATE, 3);
        start = calendar.getTime();
        end = start;
        event = getNewEvent("Allday event", start, end);
        event.setAllDay(true);
        event.setDescription("Some description.");
        event.setStyleName("color3");
        dataSource.addEvent(event);

        // Add a second allday event
        calendar.add(GregorianCalendar.DATE, 1);
        start = calendar.getTime();
        end = start;
        event = getNewEvent("Second allday event", start, end);
        event.setAllDay(true);
        event.setDescription("Some description.");
        event.setStyleName("color2");
        dataSource.addEvent(event);

        calendar.add(GregorianCalendar.DATE, -3);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 9);
        calendar.set(GregorianCalendar.MINUTE, 30);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 5);
        calendar.set(GregorianCalendar.MINUTE, 0);
        end = calendar.getTime();
        event = getNewEvent("Appointment", start, end);
        event.setWhere("Office");
        event.setStyleName("color1");
        event.setDescription("A longer description, which should display correctly.");
        dataSource.addEvent(event);

        calendar.add(GregorianCalendar.DATE, 1);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 11);
        calendar.set(GregorianCalendar.MINUTE, 0);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 8);
        end = calendar.getTime();
        event = getNewEvent("Training", start, end);
        event.setStyleName("color2");
        dataSource.addEvent(event);

        calendar.add(GregorianCalendar.DATE, 4);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 9);
        calendar.set(GregorianCalendar.MINUTE, 0);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 9);
        end = calendar.getTime();
        event = getNewEvent("Free time", start, end);
        dataSource.addEvent(event);

        calendar.setTime(originalDate);
    }

    private void initLayoutContent() {
        initNavigationButtons();
        initHideWeekEndButton();
        initReadOnlyButton();
        initDisabledButton();
        initAddNewEventButton();

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        hl.setSpacing(true);
        hl.setMargin(new MarginInfo(false, false, true, false));
        hl.addComponent(prevButton);
        hl.addComponent(captionLabel);
        hl.addComponent(monthButton);
        hl.addComponent(weekButton);
        hl.addComponent(nextButton);
        hl.setComponentAlignment(prevButton, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(monthButton, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(weekButton, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT);

        monthButton.setVisible(viewMode == Mode.WEEK);
        weekButton.setVisible(viewMode == Mode.DAY);

        HorizontalLayout controlPanel = new HorizontalLayout();
        controlPanel.setSpacing(true);
        controlPanel.setMargin(new MarginInfo(false, false, true, false));
        controlPanel.setWidth("100%");
        controlPanel.addComponent(localeSelect);
        controlPanel.addComponent(timeZoneSelect);
        controlPanel.addComponent(formatSelect);
        controlPanel.addComponent(hideWeekendsButton);
        controlPanel.addComponent(readOnlyButton);
        controlPanel.addComponent(disabledButton);
        controlPanel.addComponent(addNewEvent);

        controlPanel.setComponentAlignment(timeZoneSelect,
                Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(formatSelect, Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(localeSelect, Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(hideWeekendsButton,
                Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(readOnlyButton,
                Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(disabledButton,
                Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(addNewEvent, Alignment.MIDDLE_LEFT);

        GridLayout layout = (GridLayout) getContent();
        layout.addComponent(controlPanel);
        layout.addComponent(hl);
        layout.addComponent(calendarComponent);
        layout.setRowExpandRatio(layout.getRows() - 1, 1.0f);
    }

    private void initNavigationButtons() {
        monthButton = new Button("Month view", new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                switchToMonthView();
            }
        });

        weekButton = new Button("Week view", new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                // simulate week click
                WeekClickHandler handler = (WeekClickHandler) calendarComponent
                        .getHandler(WeekClick.EVENT_ID);
                handler.weekClick(new WeekClick(calendarComponent, calendar
                        .get(GregorianCalendar.WEEK_OF_YEAR), calendar
                        .get(GregorianCalendar.YEAR)));
            }
        });

        nextButton = new Button("Next", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                handleNextButtonClick();
            }
        });

        prevButton = new Button("Prev", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                handlePreviousButtonClick();
            }
        });
    }

    private void initHideWeekEndButton() {
        hideWeekendsButton = new CheckBox("Hide weekends");
        hideWeekendsButton.setImmediate(true);
        hideWeekendsButton
                .addValueChangeListener(new Property.ValueChangeListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        setWeekendsHidden(hideWeekendsButton.getValue());
                    }
                });
    }

    private void setWeekendsHidden(boolean weekendsHidden) {
        if (weekendsHidden) {
            int firstToShow = (GregorianCalendar.MONDAY - calendar
                    .getFirstDayOfWeek()) % 7;
            calendarComponent.setFirstVisibleDayOfWeek(firstToShow + 1);
            calendarComponent.setLastVisibleDayOfWeek(firstToShow + 5);
        } else {
            calendarComponent.setFirstVisibleDayOfWeek(1);
            calendarComponent.setLastVisibleDayOfWeek(7);
        }

    }

    private void initReadOnlyButton() {
        readOnlyButton = new CheckBox("Read-only mode");
        readOnlyButton.setImmediate(true);
        readOnlyButton
                .addValueChangeListener(new Property.ValueChangeListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        calendarComponent.setReadOnly(readOnlyButton.getValue());
                    }
                });
    }

    private void initDisabledButton() {
        disabledButton = new CheckBox("Disabled");
        disabledButton.setImmediate(true);
        disabledButton
                .addValueChangeListener(new Property.ValueChangeListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        calendarComponent.setEnabled(!disabledButton.getValue());
                    }
                });
    }

    public void initAddNewEventButton() {
        addNewEvent = new Button("Add new event");
        addNewEvent.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = -8307244759142541067L;

            @Override
            public void buttonClick(ClickEvent event) {
                Date start = getToday();
                start.setHours(0);
                start.setMinutes(0);
                start.setSeconds(0);

                Date end = getEndOfDay(calendar, start);

                showEventPopup(createNewEvent(start, end), true);
            }
        });
    }

    private void initFormFields(Layout formLayout,
            Class<? extends CalendarEvent> eventClass) {

        startDateField = createDateField("Start date");
        endDateField = createDateField("End date");

        final CheckBox allDayField = createCheckBox("All-day");
        allDayField.addValueChangeListener(new Property.ValueChangeListener() {

            private static final long serialVersionUID = -7104996493482558021L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
                if (value instanceof Boolean && Boolean.TRUE.equals(value)) {
                    setFormDateResolution(Resolution.DAY);

                } else {
                    setFormDateResolution(Resolution.MINUTE);
                }
            }

        });

        captionField = createTextField("Caption");
        final TextField whereField = createTextField("Where");
        final TextArea descriptionField = createTextArea("Description");
        descriptionField.setRows(3);

        final ComboBox styleNameField = createStyleNameComboBox();

        formLayout.addComponent(startDateField);
        formLayout.addComponent(endDateField);
        formLayout.addComponent(allDayField);
        formLayout.addComponent(captionField);
        if (eventClass == CalendarTestEvent.class) {
            formLayout.addComponent(whereField);
        }
        formLayout.addComponent(descriptionField);
        formLayout.addComponent(styleNameField);

        scheduleEventFieldGroup.bind(startDateField, "start");
        scheduleEventFieldGroup.bind(endDateField, "end");
        scheduleEventFieldGroup.bind(captionField, "caption");
        scheduleEventFieldGroup.bind(descriptionField, "description");
        if (eventClass == CalendarTestEvent.class) {
            scheduleEventFieldGroup.bind(whereField, "where");
        }
        scheduleEventFieldGroup.bind(styleNameField, "styleName");
        scheduleEventFieldGroup.bind(allDayField, "allDay");
    }

    private CheckBox createCheckBox(String caption) {
        CheckBox cb = new CheckBox(caption);
        cb.setImmediate(true);
        return cb;
    }

    private TextField createTextField(String caption) {
        TextField f = new TextField(caption);
        f.setNullRepresentation("");
        return f;
    }

    private TextArea createTextArea(String caption) {
        TextArea f = new TextArea(caption);
        f.setNullRepresentation("");
        return f;
    }

    private DateField createDateField(String caption) {
        DateField f = new DateField(caption);
        if (useSecondResolution) {
            f.setResolution(Resolution.SECOND);
        } else {
            f.setResolution(Resolution.MINUTE);
        }
        return f;
    }

    private ComboBox createStyleNameComboBox() {
        ComboBox s = new ComboBox("Color");
        s.addContainerProperty("c", String.class, "");
        s.setItemCaptionPropertyId("c");
        Item i = s.addItem("color1");
        i.getItemProperty("c").setValue("Green");
        i = s.addItem("color2");
        i.getItemProperty("c").setValue("Blue");
        i = s.addItem("color3");
        i.getItemProperty("c").setValue("Red");
        i = s.addItem("color4");
        i.getItemProperty("c").setValue("Orange");
        return s;
    }

    private void initCalendar() {
        dataSource = new BasicEventProvider();

        calendarComponent = new Calendar(dataSource);
        calendarComponent.setLocale(getLocale());
        calendarComponent.setImmediate(true);

        if (calendarWidth != null || calendarHeight != null) {
            if (calendarHeight != null) {
                calendarComponent.setHeight(calendarHeight);
            }
            if (calendarWidth != null) {
                calendarComponent.setWidth(calendarWidth);
            }
        } else {
            calendarComponent.setSizeFull();
        }

        if (firstHour != null && lastHour != null) {
            calendarComponent.setFirstVisibleHourOfDay(firstHour);
            calendarComponent.setLastVisibleHourOfDay(lastHour);
        }

        if (firstDay != null && lastDay != null) {
            calendarComponent.setFirstVisibleDayOfWeek(firstDay);
            calendarComponent.setLastVisibleDayOfWeek(lastDay);
        }

        Date today = getToday();
        calendar = new GregorianCalendar(getLocale());
        calendar.setTime(today);

        updateCaptionLabel();

        if (!showWeeklyView) {
            int rollAmount = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
            calendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
            resetTime(false);
            currentMonthsFirstDate = calendar.getTime();
            calendarComponent.setStartDate(currentMonthsFirstDate);
            calendar.add(GregorianCalendar.MONTH, 1);
            calendar.add(GregorianCalendar.DATE, -1);
            calendarComponent.setEndDate(calendar.getTime());
        }

        addCalendarEventListeners();
    }

    private Date getToday() {
        if (testBench) {
            GregorianCalendar testDate = new GregorianCalendar();
            testDate.set(GregorianCalendar.YEAR, 2000);
            testDate.set(GregorianCalendar.MONTH, 0);
            testDate.set(GregorianCalendar.DATE, 10);
            testDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
            testDate.set(GregorianCalendar.MINUTE, 0);
            testDate.set(GregorianCalendar.SECOND, 0);
            testDate.set(GregorianCalendar.MILLISECOND, 0);
            return testDate.getTime();
        }
        return new Date();
    }

    @SuppressWarnings("serial")
    private void addCalendarEventListeners() {
        // Register week clicks by changing the schedules start and end dates.
        calendarComponent.setHandler(new BasicWeekClickHandler() {

            @Override
            public void weekClick(WeekClick event) {
                // let BasicWeekClickHandler handle calendar dates, and update
                // only the other parts of UI here
                super.weekClick(event);
                updateCaptionLabel();
                switchToWeekView();
            }
        });

        calendarComponent.setHandler(new EventClickHandler() {

            @Override
            public void eventClick(EventClick event) {
                showEventPopup(event.getCalendarEvent(), false);
            }
        });

        calendarComponent.setHandler(new BasicDateClickHandler() {

            @Override
            public void dateClick(DateClickEvent event) {
                // let BasicDateClickHandler handle calendar dates, and update
                // only the other parts of UI here
                super.dateClick(event);
                switchToDayView();
            }
        });

        calendarComponent.setHandler(new RangeSelectHandler() {

            @Override
            public void rangeSelect(RangeSelectEvent event) {
                handleRangeSelect(event);
            }
        });
    }

    private ComboBox createTimeZoneSelect() {
        ComboBox s = new ComboBox("Timezone");
        s.addContainerProperty("caption", String.class, "");
        s.setItemCaptionPropertyId("caption");
        s.setFilteringMode(FilteringMode.CONTAINS);

        Item i = s.addItem(DEFAULT_ITEMID);
        i.getItemProperty("caption").setValue(
                "Default (" + TimeZone.getDefault().getID() + ")");
        for (String id : TimeZone.getAvailableIDs()) {
            if (!s.containsId(id)) {
                i = s.addItem(id);
                i.getItemProperty("caption").setValue(id);
            }
        }

        if (testBench) {
            s.select("America/New_York");
        } else {
            s.select(DEFAULT_ITEMID);
        }
        s.setImmediate(true);
        s.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {

                updateCalendarTimeZone(event.getProperty().getValue());
            }
        });

        return s;
    }

    private ComboBox createCalendarFormatSelect() {
        ComboBox s = new ComboBox("Calendar format");
        s.addContainerProperty("caption", String.class, "");
        s.setItemCaptionPropertyId("caption");

        Item i = s.addItem(DEFAULT_ITEMID);
        i.getItemProperty("caption").setValue("Default by locale");
        i = s.addItem(TimeFormat.Format12H);
        i.getItemProperty("caption").setValue("12H");
        i = s.addItem(TimeFormat.Format24H);
        i.getItemProperty("caption").setValue("24H");

        s.select(DEFAULT_ITEMID);
        s.setImmediate(true);
        s.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                updateCalendarFormat(event.getProperty().getValue());
            }
        });

        return s;
    }

    private ComboBox createLocaleSelect() {
        ComboBox s = new ComboBox("Locale");
        s.addContainerProperty("caption", String.class, "");
        s.setItemCaptionPropertyId("caption");
        s.setFilteringMode(FilteringMode.CONTAINS);

        for (Locale l : Locale.getAvailableLocales()) {
            if (!s.containsId(l)) {
                Item i = s.addItem(l);
                i.getItemProperty("caption").setValue(getLocaleItemCaption(l));
            }
        }

        s.select(getLocale());
        s.setImmediate(true);
        s.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                updateCalendarLocale((Locale) event.getProperty().getValue());
            }
        });

        return s;
    }

    private void updateCalendarTimeZone(Object timezoneId) {
        TimeZone tz = null;
        if (!DEFAULT_ITEMID.equals(timezoneId)) {
            tz = TimeZone.getTimeZone((String) timezoneId);
        }

        // remember the week that was showing, so we can re-set it later
        Date startDate = calendarComponent.getStartDate();
        calendar.setTime(startDate);
        int weekNumber = calendar.get(java.util.Calendar.WEEK_OF_YEAR);
        calendarComponent.setTimeZone(tz);
        calendar.setTimeZone(calendarComponent.getTimeZone());

        if (viewMode == Mode.WEEK) {
            calendar.set(java.util.Calendar.WEEK_OF_YEAR, weekNumber);
            calendar.set(java.util.Calendar.DAY_OF_WEEK,
                    calendar.getFirstDayOfWeek());

            calendarComponent.setStartDate(calendar.getTime());
            calendar.add(java.util.Calendar.DATE, 6);
            calendarComponent.setEndDate(calendar.getTime());
        }
    }

    private void updateCalendarFormat(Object format) {
        TimeFormat calFormat = null;
        if (format instanceof TimeFormat) {
            calFormat = (TimeFormat) format;
        }

        calendarComponent.setTimeFormat(calFormat);
    }

    private String getLocaleItemCaption(Locale l) {
        String country = l.getDisplayCountry(getLocale());
        String language = l.getDisplayLanguage(getLocale());
        StringBuilder caption = new StringBuilder(country);
        if (caption.length() != 0) {
            caption.append(", ");
        }
        caption.append(language);
        return caption.toString();
    }

    private void updateCalendarLocale(Locale l) {
        int oldFirstDayOfWeek = calendar.getFirstDayOfWeek();
        setLocale(l);
        calendarComponent.setLocale(l);
        calendar = new GregorianCalendar(l);
        int newFirstDayOfWeek = calendar.getFirstDayOfWeek();

        // we are showing 1 week, and the first day of the week has changed
        // update start and end dates so that the same week is showing
        if (viewMode == Mode.WEEK && oldFirstDayOfWeek != newFirstDayOfWeek) {
            calendar.setTime(calendarComponent.getStartDate());
            calendar.add(java.util.Calendar.DAY_OF_WEEK, 2);
            // starting at the beginning of the week
            calendar.set(GregorianCalendar.DAY_OF_WEEK, newFirstDayOfWeek);
            Date start = calendar.getTime();

            // ending at the end of the week
            calendar.add(GregorianCalendar.DATE, 6);
            Date end = calendar.getTime();

            calendarComponent.setStartDate(start);
            calendarComponent.setEndDate(end);

            // Week days depend on locale so this must be refreshed
            setWeekendsHidden(hideWeekendsButton.getValue());
        }

    }

    private void handleNextButtonClick() {
        switch (viewMode) {
        case MONTH:
            nextMonth();
            break;
        case WEEK:
            nextWeek();
            break;
        case DAY:
            nextDay();
            break;
        }
    }

    private void handlePreviousButtonClick() {
        switch (viewMode) {
        case MONTH:
            previousMonth();
            break;
        case WEEK:
            previousWeek();
            break;
        case DAY:
            previousDay();
            break;
        }
    }

    private void handleRangeSelect(RangeSelectEvent event) {
        Date start = event.getStart();
        Date end = event.getEnd();

        /*
         * If a range of dates is selected in monthly mode, we want it to end at
         * the end of the last day.
         */
        if (event.isMonthlyMode()) {
            end = getEndOfDay(calendar, end);
        }

        showEventPopup(createNewEvent(start, end), true);
    }

    private void showEventPopup(CalendarEvent event, boolean newEvent) {
        if (event == null) {
            return;
        }

        updateCalendarEventPopup(newEvent);
        updateCalendarEventForm(event);

        if (!getWindows().contains(scheduleEventPopup)) {
            addWindow(scheduleEventPopup);
        }
    }

    /* Initializes a modal window to edit schedule event. */
    private void createCalendarEventPopup() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        scheduleEventPopup = new Window(null, layout);
        scheduleEventPopup.setWidth("400px");
        scheduleEventPopup.setModal(true);
        scheduleEventPopup.center();

        layout.addComponent(scheduleEventFieldLayout);

        applyEventButton = new Button("Apply", new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    commitCalendarEvent();
                } catch (CommitException e) {
                    e.printStackTrace();
                }
            }
        });
        Button cancel = new Button("Cancel", new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                discardCalendarEvent();
            }
        });
        deleteEventButton = new Button("Delete", new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                deleteCalendarEvent();
            }
        });
        scheduleEventPopup.addCloseListener(new Window.CloseListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void windowClose(Window.CloseEvent e) {
                discardCalendarEvent();
            }
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(deleteEventButton);
        buttons.addComponent(applyEventButton);
        buttons.addComponent(cancel);
        layout.addComponent(buttons);
        layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
    }

    private void updateCalendarEventPopup(boolean newEvent) {
        if (scheduleEventPopup == null) {
            createCalendarEventPopup();
        }

        if (newEvent) {
            scheduleEventPopup.setCaption("New event");
        } else {
            scheduleEventPopup.setCaption("Edit event");
        }

        deleteEventButton.setVisible(!newEvent);
        deleteEventButton.setEnabled(!calendarComponent.isReadOnly());
        applyEventButton.setEnabled(!calendarComponent.isReadOnly());
    }

    private void updateCalendarEventForm(CalendarEvent event) {
        BeanItem<CalendarEvent> item = new BeanItem<CalendarEvent>(event);
        scheduleEventFieldLayout.removeAllComponents();
        scheduleEventFieldGroup = new FieldGroup();
        initFormFields(scheduleEventFieldLayout, event.getClass());
        scheduleEventFieldGroup.setBuffered(true);
        scheduleEventFieldGroup.setItemDataSource(item);
    }

    private void setFormDateResolution(Resolution resolution) {
        if (startDateField != null && endDateField != null) {
            startDateField.setResolution(resolution);
            endDateField.setResolution(resolution);
        }
    }

    private CalendarEvent createNewEvent(Date startDate, Date endDate) {

        BasicEvent event = new BasicEvent();
        event.setCaption("");
        event.setStart(startDate);
        event.setEnd(endDate);
        event.setStyleName("color1");
        return event;
    }

    /* Removes the event from the data source and fires change event. */
    private void deleteCalendarEvent() {
        BasicEvent event = getFormCalendarEvent();
        if (dataSource.containsEvent(event)) {
            dataSource.removeEvent(event);
        }
        removeWindow(scheduleEventPopup);
    }

    /* Adds/updates the event in the data source and fires change event. */
    private void commitCalendarEvent() throws CommitException {
        scheduleEventFieldGroup.commit();
        BasicEvent event = getFormCalendarEvent();
        if (event.getEnd() == null) {
            event.setEnd(event.getStart());
        }
        if (!dataSource.containsEvent(event)) {
            dataSource.addEvent(event);
        }

        removeWindow(scheduleEventPopup);
    }

    private void discardCalendarEvent() {
        scheduleEventFieldGroup.discard();
        removeWindow(scheduleEventPopup);
    }

    @SuppressWarnings("unchecked")
    private BasicEvent getFormCalendarEvent() {
        BeanItem<CalendarEvent> item = (BeanItem<CalendarEvent>) scheduleEventFieldGroup
                .getItemDataSource();
        CalendarEvent event = item.getBean();
        return (BasicEvent) event;
    }

    private void nextMonth() {
        rollMonth(1);
    }

    private void previousMonth() {
        rollMonth(-1);
    }

    private void nextWeek() {
        rollWeek(1);
    }

    private void previousWeek() {
        rollWeek(-1);
    }

    private void nextDay() {
        rollDate(1);
    }

    private void previousDay() {
        rollDate(-1);
    }

    private void rollMonth(int direction) {
        calendar.setTime(currentMonthsFirstDate);
        calendar.add(GregorianCalendar.MONTH, direction);
        resetTime(false);
        currentMonthsFirstDate = calendar.getTime();
        calendarComponent.setStartDate(currentMonthsFirstDate);

        updateCaptionLabel();

        calendar.add(GregorianCalendar.MONTH, 1);
        calendar.add(GregorianCalendar.DATE, -1);
        resetCalendarTime(true);
    }

    private void rollWeek(int direction) {
        calendar.add(GregorianCalendar.WEEK_OF_YEAR, direction);
        calendar.set(GregorianCalendar.DAY_OF_WEEK,
                calendar.getFirstDayOfWeek());
        resetCalendarTime(false);
        resetTime(true);
        calendar.add(GregorianCalendar.DATE, 6);
        calendarComponent.setEndDate(calendar.getTime());
    }

    private void rollDate(int direction) {
        calendar.add(GregorianCalendar.DATE, direction);
        resetCalendarTime(false);
        resetCalendarTime(true);
    }

    private void updateCaptionLabel() {
        DateFormatSymbols s = new DateFormatSymbols(getLocale());
        String month = s.getShortMonths()[calendar.get(GregorianCalendar.MONTH)];
        captionLabel.setValue(month + " "
                + calendar.get(GregorianCalendar.YEAR));
    }

    private CalendarTestEvent getNewEvent(String caption, Date start, Date end) {
        CalendarTestEvent event = new CalendarTestEvent();
        event.setCaption(caption);
        event.setStart(start);
        event.setEnd(end);

        return event;
    }

    /*
     * Switch the view to week view.
     */
    public void switchToWeekView() {
        viewMode = Mode.WEEK;
        weekButton.setVisible(false);
        monthButton.setVisible(true);
    }

    /*
     * Switch the Calendar component's start and end date range to the target
     * month only. (sample range: 01.01.2010 00:00.000 - 31.01.2010 23:59.999)
     */
    public void switchToMonthView() {
        viewMode = Mode.MONTH;
        monthButton.setVisible(false);
        weekButton.setVisible(false);

        calendar.setTime(currentMonthsFirstDate);
        calendarComponent.setStartDate(currentMonthsFirstDate);

        updateCaptionLabel();

        calendar.add(GregorianCalendar.MONTH, 1);
        calendar.add(GregorianCalendar.DATE, -1);
        resetCalendarTime(true);
    }

    /*
     * Switch to day view (week view with a single day visible).
     */
    public void switchToDayView() {
        viewMode = Mode.DAY;
        monthButton.setVisible(true);
        weekButton.setVisible(true);
    }

    private void resetCalendarTime(boolean resetEndTime) {
        resetTime(resetEndTime);
        if (resetEndTime) {
            calendarComponent.setEndDate(calendar.getTime());
        } else {
            calendarComponent.setStartDate(calendar.getTime());
            updateCaptionLabel();
        }
    }

    /*
     * Resets the calendar time (hour, minute second and millisecond) either to
     * zero or maximum value.
     */
    private void resetTime(boolean max) {
        if (max) {
            calendar.set(GregorianCalendar.HOUR_OF_DAY,
                    calendar.getMaximum(GregorianCalendar.HOUR_OF_DAY));
            calendar.set(GregorianCalendar.MINUTE,
                    calendar.getMaximum(GregorianCalendar.MINUTE));
            calendar.set(GregorianCalendar.SECOND,
                    calendar.getMaximum(GregorianCalendar.SECOND));
            calendar.set(GregorianCalendar.MILLISECOND,
                    calendar.getMaximum(GregorianCalendar.MILLISECOND));
        } else {
            calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
            calendar.set(GregorianCalendar.MINUTE, 0);
            calendar.set(GregorianCalendar.SECOND, 0);
            calendar.set(GregorianCalendar.MILLISECOND, 0);
        }
    }

    private static Date getEndOfDay(java.util.Calendar calendar, Date date) {
        java.util.Calendar calendarClone = (java.util.Calendar) calendar
                .clone();

        calendarClone.setTime(date);
        calendarClone.set(java.util.Calendar.MILLISECOND,
                calendarClone.getActualMaximum(java.util.Calendar.MILLISECOND));
        calendarClone.set(java.util.Calendar.SECOND,
                calendarClone.getActualMaximum(java.util.Calendar.SECOND));
        calendarClone.set(java.util.Calendar.MINUTE,
                calendarClone.getActualMaximum(java.util.Calendar.MINUTE));
        calendarClone.set(java.util.Calendar.HOUR,
                calendarClone.getActualMaximum(java.util.Calendar.HOUR));
        calendarClone.set(java.util.Calendar.HOUR_OF_DAY,
                calendarClone.getActualMaximum(java.util.Calendar.HOUR_OF_DAY));

        return calendarClone.getTime();
    }

    private static Date getStartOfDay(java.util.Calendar calendar, Date date) {
        java.util.Calendar calendarClone = (java.util.Calendar) calendar
                .clone();

        calendarClone.setTime(date);
        calendarClone.set(java.util.Calendar.MILLISECOND, 0);
        calendarClone.set(java.util.Calendar.SECOND, 0);
        calendarClone.set(java.util.Calendar.MINUTE, 0);
        calendarClone.set(java.util.Calendar.HOUR, 0);
        calendarClone.set(java.util.Calendar.HOUR_OF_DAY, 0);

        return calendarClone.getTime();
    }
}
