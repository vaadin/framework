/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.featurebrowser;

import java.util.Random;

import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.AbstractSelect.Filtering;

/**
 * 
 */
public class ComboBoxExample extends CustomComponent {

    private static final String[] firstnames = new String[] { "John", "Mary",
            "Joe", "Sarah", "Jeff", "Jane", "Peter", "Marc", "Robert", "Paula",
            "Lenny", "Kenny", "Nathan", "Nicole", "Laura", "Jos", "Josie",
            "Linus" };

    private static final String[] lastnames = new String[] { "Torvalds",
            "Smith", "Adams", "Black", "Wilson", "Richards", "Thompson",
            "McGoff", "Halas", "Jones", "Beck", "Sheridan", "Picard", "Hill",
            "Fielding", "Einstein" };

    public ComboBoxExample() {
        final VerticalLayout main = new VerticalLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        // starts-with filter
        final ComboBox s1 = new ComboBox("Select with starts-with filter");
        s1.setFilteringMode(Filtering.FILTERINGMODE_STARTSWITH);
        s1.setWidth("20em");
        Random r = new Random(5);
        for (int i = 0; i < 105; i++) {
            s1
                    .addItem(firstnames[(int) (r.nextDouble() * (firstnames.length - 1))]
                            + " "
                            + lastnames[(int) (r.nextDouble() * (lastnames.length - 1))]);
        }
        s1.setImmediate(true);
        main.addComponent(s1);

        // contains filter
        final ComboBox s2 = new ComboBox("Select with contains filter");
        s2.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        s2.setWidth("20em");
        for (int i = 0; i < 500; i++) {
            s2
                    .addItem(firstnames[(int) (r.nextDouble() * (firstnames.length - 1))]
                            + " "
                            + lastnames[(int) (r.nextDouble() * (lastnames.length - 1))]);
        }
        s2.setImmediate(true);
        main.addComponent(s2);

        // initially empty
        final ComboBox s3 = new ComboBox("Initially empty; enter your own");
        s3.setWidth("20em");
        s3.setImmediate(true);
        s3.setNewItemsAllowed(true);
        main.addComponent(s3);

    }

}
