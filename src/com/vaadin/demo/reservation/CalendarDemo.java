/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.reservation;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.QueryContainer;
import com.vaadin.demo.util.SampleCalendarDatabase;
import com.vaadin.ui.DateField;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;

/**
 * This example shows how the CalendarField can use Containers. A QueryContainer
 * is used to bind SQL table rows to the calendar. Demonstrates: how to create
 * <code>com.vaadin.data.Container</code> and set it as datasource for
 * <code>com.vaadin.ui.Component.CalendarField</code>
 * 
 * @author IT Mill Ltd.
 * @since 4.0.0
 * 
 */
public class CalendarDemo extends com.vaadin.Application {

    // Database provided with sample data
    private SampleCalendarDatabase sampleDatabase;

    // The calendar UI component
    private CalendarField from;
    private CalendarField to;

    /**
     * Initialize Application. Demo components are added to main window.
     */
    @Override
    public void init() {
        final Window main = new Window("Calendar demo");
        setMainWindow(main);

        main.setLayout(new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL));

        // create the calendar component and add to layout
        from = new CalendarField();
        main.addComponent(from);
        from.setResolution(DateField.RESOLUTION_HOUR);
        from.setImmediate(true);

        to = new CalendarField();
        main.addComponent(to);
        to.setResolution(DateField.RESOLUTION_HOUR);
        to.setEnabled(false);
        to.setImmediate(true);

        from.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                final Date fd = (Date) from.getValue();
                final Date td = (Date) to.getValue();
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
        from.setMinimumDate(Calendar.getInstance().getTime());

    }

    /**
     * Populates table component with all rows from calendar table.
     */
    private void initCalendars() {
        try {
            final QueryContainer qc = new QueryContainer("SELECT * FROM "
                    + SampleCalendarDatabase.DB_TABLE_NAME, sampleDatabase
                    .getConnection());
            from.setContainerDataSource(qc);
            to.setContainerDataSource(qc);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        /*
         * // Calendar will use the first date property as start if you do not
         * // explicitly specify the property id. Our start -property will be
         * the // first one, so it's intentionally left out. // Start is the
         * only mandatory property, but you'll probably want to // specify title
         * as well.
         * from.setItemEndPropertyId(SampleCalendarDatabase.PROPERTY_ID_END);
         * from
         * .setItemTitlePropertyId(SampleCalendarDatabase.PROPERTY_ID_TITLE);
         * from
         * .setItemNotimePropertyId(SampleCalendarDatabase.PROPERTY_ID_NOTIME);
         * 
         * to.setItemEndPropertyId(SampleCalendarDatabase.PROPERTY_ID_END);
         * to.setItemTitlePropertyId(SampleCalendarDatabase.PROPERTY_ID_TITLE);
         * to
         * .setItemNotimePropertyId(SampleCalendarDatabase.PROPERTY_ID_NOTIME);
         */
    }

}
