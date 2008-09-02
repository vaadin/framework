/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.book;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;

public class TableExample2 extends CustomComponent {
    /* A layout needed for the example. */
    OrderedLayout layout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);

    TableExample2() {
        setCompositionRoot(layout);

        /* Create the table with a caption. */
        final Table table = new Table();

        /*
         * Define the names and data types of columns. The "default value"
         * parameter is meaningless here.
         */
        table.addContainerProperty("First Name", String.class, null);
        table.addContainerProperty("Last Name", String.class, null);
        table.addContainerProperty("Year", Integer.class, null);

        /* Add a few items in the table. */
        table.addItem(new Object[] { "Nicolaus", "Copernicus",
                new Integer(1473) }, new Integer(1));
        table.addItem(new Object[] { "Tycho", "Brahe", new Integer(1546) },
                new Integer(2));
        table.addItem(new Object[] { "Giordano", "Bruno", new Integer(1548) },
                new Integer(3));
        table.addItem(new Object[] { "Galileo", "Galilei", new Integer(1564) },
                new Integer(4));
        table.addItem(new Object[] { "Johannes", "Kepler", new Integer(1571) },
                new Integer(5));
        table.addItem(new Object[] { "Isaac", "Newton", new Integer(1643) },
                new Integer(6));

        /* Set number of visible rows. */
        table.setPageLength(5);

        /* Allow selecting items from the table. */
        table.setSelectable(true);

        /*
         * When an item is selected, the selection is sent immediately to
         * server.
         */
        table.setImmediate(true);

        /* Feedback from selection. */
        final Label current = new Label("Selected: -");

        /* Handle selection change. */
        table.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                current.setValue("Selected: " + table.getValue());
            }
        });

        table.setNullSelectionAllowed(false);

        layout.addComponent(table);
        layout.addComponent(current);
    }
}
