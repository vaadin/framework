package com.itmill.toolkit.demo;

import java.sql.SQLException;
import java.util.Date;

import com.itmill.toolkit.data.util.QueryContainer;
import com.itmill.toolkit.demo.util.SampleCalendarDatabase;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CalendarField;
import com.itmill.toolkit.ui.Window;

/**
 * This example shows how the CalendarField can use Containers. A QueryContainer
 * is used to bind SQL table rows to the calendar. Demonstrates: how to create
 * <code>com.itmill.toolkit.data.Container</code> and set it as datasource for
 * <code>com.itmill.toolkit.ui.Component.CalendarField</code>
 * 
 * @author IT Mill Ltd.
 * @since 4.0.0
 * 
 */
public class CalendarDemo extends com.itmill.toolkit.Application {

    // Database provided with sample data
    private SampleCalendarDatabase sampleDatabase;

    // The calendar UI component
    private CalendarField calendar;

    /**
     * Initialize Application. Demo components are added to main window.
     */
    public void init() {
	Window main = new Window("Calendar demo");
	setMainWindow(main);

	// set the application to use Corporate -theme
	setTheme("corporate");

	// create the calendar component and add to layout
	calendar = new CalendarField();
	main.addComponent(calendar);
	calendar.setResolution(CalendarField.RESOLUTION_HOUR);

	// initialize the sample database and set as calendar datasource
	sampleDatabase = new SampleCalendarDatabase();
	initCalendar();
	
	// Don't allow dates before today
	calendar.setMinimumDate(new Date());
	
    }

    /**
     * Populates table component with all rows from calendar table.
     */
    private void initCalendar() {
	try {
	    QueryContainer qc = new QueryContainer("SELECT * FROM "
		    + SampleCalendarDatabase.DB_TABLE_NAME, sampleDatabase
		    .getConnection());
	    calendar.setContainerDataSource(qc);
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	// Calendar will use the first date property as start if you do not
	// explicitly specify the property id. Our start -property will be the
	// first one, so it's intentionally left out.
	// Start is the only mandatory property, but you'll probably want to
	// specify title as well.
	calendar.setItemEndPropertyId(SampleCalendarDatabase.PROPERTY_ID_END);
	calendar
		.setItemTitlePropertyId(SampleCalendarDatabase.PROPERTY_ID_TITLE);
	calendar
		.setItemNotimePropertyId(SampleCalendarDatabase.PROPERTY_ID_NOTIME);
    }

}
