package com.itmill.toolkit.demo.featurebrowser;

import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.OrderedLayout;
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
        OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        // starts-with filter
        ComboBox s1 = new ComboBox("Select with starts-with filter");
        s1.setFilteringMode(Filtering.FILTERINGMODE_STARTSWITH);
        s1.setColumns(20);
        for (int i = 0; i < 105; i++) {
            s1
                    .addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
                            + " "
                            + lastnames[(int) (Math.random() * (lastnames.length - 1))]);
        }
        s1.setImmediate(true);
        main.addComponent(s1);

        // contains filter
        ComboBox s2 = new ComboBox("Select with contains filter");
        s2.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        s2.setColumns(20);
        for (int i = 0; i < 500; i++) {
            s2
                    .addItem(firstnames[(int) (Math.random() * (firstnames.length - 1))]
                            + " "
                            + lastnames[(int) (Math.random() * (lastnames.length - 1))]);
        }
        s2.setImmediate(true);
        main.addComponent(s2);

        // initially empty
        ComboBox s3 = new ComboBox("Initially empty; enter your own");
        s3.setColumns(20);
        s3.setImmediate(true);
        main.addComponent(s3);

    }

}
