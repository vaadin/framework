/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.magi;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;

public class TableExample extends CustomComponent {
    /* Create the table with a caption. */
    Table table = new Table("This is my Table");

    /* A layout needed for the example. */
    OrderedLayout layout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);

    /* Feedback for selecting items from the table. */
    Label current = new Label("Selected: -");

    TableExample() {
        setCompositionRoot(layout);
        layout.addComponent(table);

        /* Define the names, data types, and default values of columns. */
        table.addContainerProperty("First Name", String.class,
                "(no first name)");
        table.addContainerProperty("Last Name", String.class, "(no last name)");
        table.addContainerProperty("Year", Integer.class, null);

        /* We use these entries to generate random items in a table. */
        final String[] firstnames = new String[] { "Donald", "Patty", "Sally",
                "Douglas" };
        final String[] lastnames = new String[] { "Smith", "Jones", "Adams",
                "Knuth" };

        /* Add some items in the table and assign them an Item ID (IID). */
        for (int i = 0; i < 500; i++) {
            /* Add a randomly generated item in the Table. */
            table.addItem(new Object[] {
                        firstnames[(int) (Math.random() * (firstnames.length - 0.01))],
                        lastnames[(int) (Math.random() * (lastnames.length - 0.01))],
                        new Integer((int) (1900 + Math.random() * 100)) },
                    new Integer(i));
        }

        /* Set the number of items visible in the table. */
        table.setPageLength(10);

        /* Enable some UI features for the table. */
        table.setColumnReorderingAllowed(true);
        table.setColumnCollapsingAllowed(true);

        /* Allow selecting items from the table. */
        table.setSelectable(true);
        
        /* When an item is selected, the selection is sent immediately to server. */
        table.setImmediate(true);
        
        /* Handle selection change. */
        table.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                current.setValue("Selected: " + table.getValue().toString());
            }
        });
        
        layout.addComponent(current);
    }
}
