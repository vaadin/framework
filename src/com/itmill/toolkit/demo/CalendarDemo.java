package com.itmill.toolkit.demo;

import java.sql.SQLException;
import java.util.Date;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.data.util.QueryContainer;
import com.itmill.toolkit.demo.util.SampleCalendarDatabase;
import com.itmill.toolkit.ui.CalendarField;
import com.itmill.toolkit.ui.OrderedLayout;
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
	private CalendarField from;
	private CalendarField to;

	/**
	 * Initialize Application. Demo components are added to main window.
	 */
	public void init() {
		Window main = new Window("Calendar demo");
		setMainWindow(main);

		main.setLayout(new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL));

		// set the application to use Corporate -theme
		setTheme("corporate");

		// create the calendar component and add to layout
		from = new CalendarField();
		main.addComponent(from);
		from.setResolution(CalendarField.RESOLUTION_HOUR);
		from.setImmediate(true);

		to = new CalendarField();
		main.addComponent(to);
		to.setResolution(CalendarField.RESOLUTION_HOUR);
		to.setEnabled(false);
		to.setImmediate(true);

		from.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				Date fd = (Date) from.getValue();
				Date td = (Date) to.getValue();
				if (fd == null) {
					to.setValue(null);
					to.setEnabled(false);
					return;
				} else {
					to.setEnabled(true);
				}
				to.setMinimumDate(fd);
				if (td == null || td.before(fd)) {
					to.setValue(fd);
				}
			}
		});

		// initialize the sample database and set as calendar datasource
		sampleDatabase = new SampleCalendarDatabase();
		initCalendars();

		// Don't allow dates before today
		from.setMinimumDate(new Date());

	}

	/**
	 * Populates table component with all rows from calendar table.
	 */
	private void initCalendars() {
		try {
			QueryContainer qc = new QueryContainer("SELECT * FROM "
					+ SampleCalendarDatabase.DB_TABLE_NAME, sampleDatabase
					.getConnection());
			from.setContainerDataSource(qc);
			to.setContainerDataSource(qc);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Calendar will use the first date property as start if you do not
		// explicitly specify the property id. Our start -property will be the
		// first one, so it's intentionally left out.
		// Start is the only mandatory property, but you'll probably want to
		// specify title as well.
		from.setItemEndPropertyId(SampleCalendarDatabase.PROPERTY_ID_END);
		from.setItemTitlePropertyId(SampleCalendarDatabase.PROPERTY_ID_TITLE);
		from.setItemNotimePropertyId(SampleCalendarDatabase.PROPERTY_ID_NOTIME);

		to.setItemEndPropertyId(SampleCalendarDatabase.PROPERTY_ID_END);
		to.setItemTitlePropertyId(SampleCalendarDatabase.PROPERTY_ID_TITLE);
		to.setItemNotimePropertyId(SampleCalendarDatabase.PROPERTY_ID_NOTIME);

	}

}
