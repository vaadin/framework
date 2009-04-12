/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.automatedtests;

import java.util.Date;
import java.util.Random;
import java.util.Vector;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class ComponentsInTable extends CustomComponent {

    private static final long serialVersionUID = 7179313717613510935L;

    public ComponentsInTable(int cols, int rows) {
        final OrderedLayout main = new OrderedLayout();
        setCompositionRoot(main);

        main.addComponent(getTestTable(cols, rows));
    }

    public static Table getTestTable(int cols, int rows) {
        Random rnd = new Random(1);

        final Table t = new Table();
        t.setColumnCollapsingAllowed(true);
        for (int i = 0; i < cols; i++) {
            t.addContainerProperty(testString[i], String.class, "");
        }
        t.addContainerProperty("button", Button.class, null);
        for (int i = 0; i < rows; i++) {
            final Vector content = new Vector();
            for (int j = 0; j < cols; j++) {
                content.add(rndString(rnd));
            }
            content.add(new Button("b" + i, new Button.ClickListener() {

                public void buttonClick(ClickEvent event) {
                    Button b = event.getButton();
                    System.out.println(event.getButton().getCaption()
                            + " click: " + (new Date()).toGMTString());
                    System.out.println(event.getButton().getApplication());

                }
            }));
            t.addItem(content.toArray(), "" + i);
        }
        t.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        return t;
    }

    static String[] testString = new String[] { "Jacob", "Michael", "Joshua",
            "Matthew", "Ethan", "Andrew", "Daniel", "Anthony", "Christopher",
            "Joseph", "William", "Alexander", "Ryan", "David", "Nicholas",
            "Tyler", "James", "John", "Jonathan", "Nathan", "Samuel",
            "Christian", "Noah", "Dylan", "Benjamin", "Logan", "Brandon",
            "Gabriel", "Zachary", "Jose", "Elijah", "Angel", "Kevin", "Jack",
            "Caleb", "Justin", "Austin", "Evan", "Robert", "Thomas", "Luke",
            "Mason", "Aidan", "Jackson", "Isaiah", "Jordan", "Gavin", "Connor",
            "Aiden", "Isaac", "Jason", "Cameron", "Hunter", "Jayden", "Juan",
            "Charles", "Aaron", "Lucas", "Luis", "Owen", "Landon", "Diego",
            "Brian", "Adam", "Adrian", "Kyle", "Eric", "Ian", "Nathaniel",
            "Carlos", "Alex", "Bryan", "Jesus", "Julian", "Sean", "Carter",
            "Hayden", "Jeremiah", "Cole", "Brayden", "Wyatt", "Chase",
            "Steven", "Timothy", "Dominic", "Sebastian", "Xavier", "Jaden",
            "Jesse", "Devin", "Seth", "Antonio", "Richard", "Miguel", "Colin",
            "Cody", "Alejandro", "Caden", "Blake", "Carson" };

    public static String rndString(Random rnd) {
        return testString[(int) (rnd.nextDouble() * testString.length)];
    }

}
