/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo;

import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.AbstractSelect.Filtering;

/**
 * The classic "hello, world!" example for IT Mill Toolkit. The class simply
 * implements the abstract {@link com.itmill.toolkit.Application#init() init()}
 * method in which it creates a Window and adds a Label to it.
 * 
 * @author IT Mill Ltd.
 * @see com.itmill.toolkit.Application
 * @see com.itmill.toolkit.ui.Window
 * @see com.itmill.toolkit.ui.Label
 */
public class FilterSelect extends com.itmill.toolkit.Application {

    private static final String[] firstnames = new String[] { "John", "Mary",
            "Joe", "Sarah", "Jeff", "Jane", "Peter", "Marc", "Robert", "Paula",
            "Lenny", "Kenny", "Nathan", "Nicole", "Laura", "Jos", "Josie",
            "Linus" };

    private static final String[] lastnames = new String[] { "Torvalds",
            "Smith", "Adams", "Black", "Wilson", "Richards", "Thompson",
            "McGoff", "Halas", "Jones", "Beck", "Sheridan", "Picard", "Hill",
            "Fielding", "Einstein" };

    /**
     * The initialization method that is the only requirement for inheriting the
     * com.itmill.toolkit.service.Application class. It will be automatically
     * called by the framework when a user accesses the application.
     */
    public void init() {

        /*
         * - Create new window for the application - Give the window a visible
         * title - Set the window to be the main window of the application
         */
        final Window main = new Window("Filter select demo");
        setMainWindow(main);

        // default filterin (Starts with)
        final Select s1 = new Select();
        for (int i = 0; i < 105; i++) {
            s1
                    .addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
                            + " "
                            + lastnames[(int) (Math.random() * (lastnames.length - 1))]);
        }
        s1.setImmediate(true);

        // contains filter
        final Select s2 = new Select();
        for (int i = 0; i < 500; i++) {
            s2
                    .addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
                            + " "
                            + lastnames[(int) (Math.random() * (lastnames.length - 1))]);
        }
        s2.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);

        // Add selects to UI using ordered layout and panels
        final OrderedLayout orderedLayout = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);

        final Panel panel1 = new Panel("Select with default filter");
        final Panel panel2 = new Panel("Select with contains filter");

        panel1.addComponent(s1);
        panel2.addComponent(s2);

        orderedLayout.addComponent(panel1);
        orderedLayout.addComponent(panel2);
        main.addComponent(orderedLayout);

    }

}
