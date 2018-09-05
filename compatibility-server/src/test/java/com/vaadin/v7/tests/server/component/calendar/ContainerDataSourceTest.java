package com.vaadin.v7.tests.server.component.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.data.Container.Sortable;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Calendar;
import com.vaadin.v7.ui.components.calendar.ContainerEventProvider;
import com.vaadin.v7.ui.components.calendar.event.BasicEvent;
import com.vaadin.v7.ui.components.calendar.event.CalendarEvent;

public class ContainerDataSourceTest {

    private Calendar calendar;

    @Before
    public void setUp() {
        calendar = new Calendar();
    }

    /**
     * Tests adding a bean item container to the Calendar
     */
    @Test
    public void testWithBeanItemContainer() {

        // Create a container to use as a datasource
        Indexed container = createTestBeanItemContainer();

        // Set datasource
        calendar.setContainerDataSource(container);

        // Start and end dates to query for
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(((CalendarEvent) container.getIdByIndex(0)).getStart());
        Date start = cal.getTime();
        cal.add(java.util.Calendar.MONTH, 1);
        Date end = cal.getTime();

        // Test the all events are returned
        List<CalendarEvent> events = calendar.getEventProvider()
                .getEvents(start, end);
        assertEquals(container.size(), events.size());

        // Test that a certain range is returned
        cal.setTime(((CalendarEvent) container.getIdByIndex(6)).getStart());
        end = cal.getTime();
        events = calendar.getEventProvider().getEvents(start, end);
        assertEquals(6, events.size());
    }

    /**
     * This tests tests that if you give the Calendar an unsorted (== not sorted
     * by starting date) container then the calendar should gracefully handle
     * it. In this case the size of the container will be wrong. The test is
     * exactly the same as {@link #testWithBeanItemContainer()} except that the
     * beans has been intentionally sorted by caption instead of date.
     */
    @Test
    public void testWithUnsortedBeanItemContainer() {
        // Create a container to use as a datasource
        Indexed container = createTestBeanItemContainer();

        // Make the container sorted by caption
        ((Sortable) container).sort(new Object[] { "caption" },
                new boolean[] { true });

        // Set data source
        calendar.setContainerDataSource(container);

        // Start and end dates to query for
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(((CalendarEvent) container.getIdByIndex(0)).getStart());
        Date start = cal.getTime();
        cal.add(java.util.Calendar.MONTH, 1);
        Date end = cal.getTime();

        // Test the all events are returned
        List<CalendarEvent> events = calendar.getEventProvider()
                .getEvents(start, end);
        assertEquals(container.size(), events.size());

        // Test that a certain range is returned
        cal.setTime(((CalendarEvent) container.getIdByIndex(6)).getStart());
        end = cal.getTime();
        events = calendar.getEventProvider().getEvents(start, end);

        // The events size is 1 since the getEvents returns the wrong range
        assertEquals(1, events.size());
    }

    /**
     * Tests adding a Indexed container to the Calendar
     */
    @Test
    public void testWithIndexedContainer() {

        // Create a container to use as a datasource
        Indexed container = createTestIndexedContainer();

        // Set datasource
        calendar.setContainerDataSource(container, "testCaption",
                "testDescription", "testStartDate", "testEndDate", null);

        // Start and end dates to query for
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime((Date) container.getItem(container.getIdByIndex(0))
                .getItemProperty("testStartDate").getValue());
        Date start = cal.getTime();
        cal.add(java.util.Calendar.MONTH, 1);
        Date end = cal.getTime();

        // Test the all events are returned
        List<CalendarEvent> events = calendar.getEventProvider()
                .getEvents(start, end);
        assertEquals(container.size(), events.size());

        // Check that event values are present
        CalendarEvent e = events.get(0);
        assertEquals("Test 1", e.getCaption());
        assertEquals("Description 1", e.getDescription());
        assertTrue(e.getStart().compareTo(start) == 0);

        // Test that a certain range is returned
        cal.setTime((Date) container.getItem(container.getIdByIndex(6))
                .getItemProperty("testStartDate").getValue());
        end = cal.getTime();
        events = calendar.getEventProvider().getEvents(start, end);
        assertEquals(6, events.size());
    }

    @Test
    public void testNullLimitsBeanItemContainer() {
        // Create a container to use as a datasource
        Indexed container = createTestBeanItemContainer();

        // Start and end dates to query for
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(((CalendarEvent) container.getIdByIndex(0)).getStart());
        Date start = cal.getTime();
        cal.add(java.util.Calendar.MONTH, 1);
        Date end = cal.getTime();

        // Set datasource
        calendar.setContainerDataSource(container);

        // Test null start time
        List<CalendarEvent> events = calendar.getEventProvider().getEvents(null,
                end);
        assertEquals(container.size(), events.size());

        // Test null end time
        events = calendar.getEventProvider().getEvents(start, null);
        assertEquals(container.size(), events.size());

        // Test both null times
        events = calendar.getEventProvider().getEvents(null, null);
        assertEquals(container.size(), events.size());
    }

    @Test
    public void testNullLimitsIndexedContainer() {
        // Create a container to use as a datasource
        Indexed container = createTestIndexedContainer();

        // Start and end dates to query for
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime((Date) container.getItem(container.getIdByIndex(0))
                .getItemProperty("testStartDate").getValue());
        Date start = cal.getTime();
        cal.add(java.util.Calendar.MONTH, 1);
        Date end = cal.getTime();

        // Set datasource
        calendar.setContainerDataSource(container, "testCaption",
                "testDescription", "testStartDate", "testEndDate", null);

        // Test null start time
        List<CalendarEvent> events = calendar.getEventProvider().getEvents(null,
                end);
        assertEquals(container.size(), events.size());

        // Test null end time
        events = calendar.getEventProvider().getEvents(start, null);
        assertEquals(container.size(), events.size());

        // Test both null times
        events = calendar.getEventProvider().getEvents(null, null);
        assertEquals(container.size(), events.size());
    }

    /**
     * Tests the addEvent convenience method with the default event provider
     */
    @Test
    public void testAddEventConvinienceMethod() {

        // Start and end dates to query for
        java.util.Calendar cal = java.util.Calendar.getInstance();
        Date start = cal.getTime();
        cal.add(java.util.Calendar.MONTH, 1);
        Date end = cal.getTime();

        // Ensure no events
        assertEquals(0, calendar.getEvents(start, end).size());

        // Add an event
        BasicEvent event = new BasicEvent("Test", "Test", start);
        calendar.addEvent(event);

        // Ensure event exists
        List<CalendarEvent> events = calendar.getEvents(start, end);
        assertEquals(1, events.size());
        assertEquals(events.get(0).getCaption(), event.getCaption());
        assertEquals(events.get(0).getDescription(), event.getDescription());
        assertEquals(events.get(0).getStart(), event.getStart());
    }

    /**
     * Test the removeEvent convenience method with the default event provider
     */
    @Test
    public void testRemoveEventConvinienceMethod() {

        // Start and end dates to query for
        java.util.Calendar cal = java.util.Calendar.getInstance();
        Date start = cal.getTime();
        cal.add(java.util.Calendar.MONTH, 1);
        Date end = cal.getTime();

        // Ensure no events
        assertEquals(0, calendar.getEvents(start, end).size());

        // Add an event
        CalendarEvent event = new BasicEvent("Test", "Test", start);
        calendar.addEvent(event);

        // Ensure event exists
        assertEquals(1, calendar.getEvents(start, end).size());

        // Remove event
        calendar.removeEvent(event);

        // Ensure no events
        assertEquals(0, calendar.getEvents(start, end).size());
    }

    @Test
    public void testAddEventConvinienceMethodWithCustomEventProvider() {

        // Use a container data source
        calendar.setEventProvider(new ContainerEventProvider(
                new BeanItemContainer<BasicEvent>(BasicEvent.class)));

        // Start and end dates to query for
        java.util.Calendar cal = java.util.Calendar.getInstance();
        Date start = cal.getTime();
        cal.add(java.util.Calendar.MONTH, 1);
        Date end = cal.getTime();

        // Ensure no events
        assertEquals(0, calendar.getEvents(start, end).size());

        // Add an event
        BasicEvent event = new BasicEvent("Test", "Test", start);
        calendar.addEvent(event);

        // Ensure event exists
        List<CalendarEvent> events = calendar.getEvents(start, end);
        assertEquals(1, events.size());
        assertEquals(events.get(0).getCaption(), event.getCaption());
        assertEquals(events.get(0).getDescription(), event.getDescription());
        assertEquals(events.get(0).getStart(), event.getStart());
    }

    @Test
    public void testRemoveEventConvinienceMethodWithCustomEventProvider() {

        // Use a container data source
        calendar.setEventProvider(new ContainerEventProvider(
                new BeanItemContainer<BasicEvent>(BasicEvent.class)));

        // Start and end dates to query for
        java.util.Calendar cal = java.util.Calendar.getInstance();
        Date start = cal.getTime();
        cal.add(java.util.Calendar.MONTH, 1);
        Date end = cal.getTime();

        // Ensure no events
        assertEquals(0, calendar.getEvents(start, end).size());

        // Add an event
        BasicEvent event = new BasicEvent("Test", "Test", start);
        calendar.addEvent(event);

        // Ensure event exists
        List<CalendarEvent> events = calendar.getEvents(start, end);
        assertEquals(1, events.size());

        // Remove event
        calendar.removeEvent(event);

        // Ensure no events
        assertEquals(0, calendar.getEvents(start, end).size());
    }

    @Test
    public void testStyleNamePropertyRetrieved() {
        IndexedContainer ic = (IndexedContainer) createTestIndexedContainer();
        ic.addContainerProperty("testStyleName", String.class, "");
        for (int i = 0; i < 10; i++) {
            Item item = ic.getItem(ic.getIdByIndex(i));
            @SuppressWarnings("unchecked")
            Property<String> itemProperty = item
                    .getItemProperty("testStyleName");
            itemProperty.setValue("testStyle");
        }

        ContainerEventProvider provider = new ContainerEventProvider(ic);
        provider.setCaptionProperty("testCaption");
        provider.setDescriptionProperty("testDescription");
        provider.setStartDateProperty("testStartDate");
        provider.setEndDateProperty("testEndDate");
        provider.setStyleNameProperty("testStyleName");

        calendar.setEventProvider(provider);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(java.util.Calendar.DAY_OF_MONTH, 20);
        Date then = cal.getTime();
        List<CalendarEvent> events = calendar.getEventProvider().getEvents(now,
                then);
        for (CalendarEvent ce : events) {
            assertEquals("testStyle", ce.getStyleName());
        }
    }

    @Test
    public void testAutomaticScaleVisibleHoursOfDay() {
        BeanItemContainer<CalendarEvent> container = new BeanItemContainer<CalendarEvent>(
                CalendarEvent.class);
        java.util.Calendar start = java.util.Calendar.getInstance();
        java.util.Calendar end = java.util.Calendar.getInstance();

        start.set(java.util.Calendar.HOUR_OF_DAY, 8);
        start.set(java.util.Calendar.MINUTE, 10);
        // same start and end time
        container.addBean(
                new BasicEvent("8:00", "Description 1", start.getTime()));

        start.set(java.util.Calendar.HOUR_OF_DAY, 16);
        end.set(java.util.Calendar.HOUR_OF_DAY, 18);
        end.set(java.util.Calendar.MINUTE, 10);

        container.addBean(new BasicEvent("16-18", "Description 2",
                start.getTime(), end.getTime())); // 16-18

        calendar.setContainerDataSource(container);
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setLocale(Locale.getDefault());
        calendar.beforeClientResponse(true); // simulate adding to UI

        assertEquals(0, calendar.getFirstVisibleHourOfDay());
        assertEquals(23, calendar.getLastVisibleHourOfDay());

        calendar.autoScaleVisibleHoursOfDay();
        assertEquals(8, calendar.getFirstVisibleHourOfDay());
        assertEquals(18, calendar.getLastVisibleHourOfDay());

        // reset visible timing to something else, so that the added event is
        // not filtered out
        calendar.resetVisibleHoursOfDay();
        calendar.beforeClientResponse(false); // simulate being attached

        assertEquals(0, calendar.getFirstVisibleHourOfDay());
        assertEquals(23, calendar.getLastVisibleHourOfDay());

        start.set(java.util.Calendar.HOUR_OF_DAY, 5);
        end.set(java.util.Calendar.HOUR_OF_DAY, 21);
        container.addBean(new BasicEvent("05-21", "Description 3",
                start.getTime(), end.getTime())); // 05-21

        calendar.beforeClientResponse(false); // simulate being attached
        calendar.autoScaleVisibleHoursOfDay();
        assertEquals(5, calendar.getFirstVisibleHourOfDay());
        assertEquals(21, calendar.getLastVisibleHourOfDay());
    }

    private static Indexed createTestBeanItemContainer() {
        BeanItemContainer<CalendarEvent> eventContainer = new BeanItemContainer<CalendarEvent>(
                CalendarEvent.class);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        for (int i = 1; i <= 10; i++) {
            eventContainer.addBean(new BasicEvent("Test " + i,
                    "Description " + i, cal.getTime()));
            cal.add(java.util.Calendar.DAY_OF_MONTH, 2);
        }
        return eventContainer;
    }

    private static Indexed createTestIndexedContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("testCaption", String.class, "");
        container.addContainerProperty("testDescription", String.class, "");
        container.addContainerProperty("testStartDate", Date.class, null);
        container.addContainerProperty("testEndDate", Date.class, null);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        for (int i = 1; i <= 10; i++) {
            Item item = container.getItem(container.addItem());
            item.getItemProperty("testCaption").setValue("Test " + i);
            item.getItemProperty("testDescription")
                    .setValue("Description " + i);
            item.getItemProperty("testStartDate").setValue(cal.getTime());
            item.getItemProperty("testEndDate").setValue(cal.getTime());
            cal.add(java.util.Calendar.DAY_OF_MONTH, 2);
        }
        return container;
    }

}
