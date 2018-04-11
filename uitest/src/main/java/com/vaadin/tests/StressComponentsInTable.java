package com.vaadin.tests;

import java.util.Date;
import java.util.Vector;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

public class StressComponentsInTable extends CustomComponent {

    public StressComponentsInTable() {
        final VerticalLayout main = new VerticalLayout();
        setCompositionRoot(main);

        main.addComponent(getTestTable(4, 1000));

    }

    public static Table getTestTable(int cols, int rows) {
        final Table t = new Table();
        t.setColumnCollapsingAllowed(true);
        for (int i = 0; i < cols; i++) {
            t.addContainerProperty(testString[i], String.class, "");
        }
        t.addContainerProperty("button", Button.class, null);
        for (int i = 0; i < rows; i++) {
            final Vector<Object> content = new Vector<>();
            for (int j = 0; j < cols; j++) {
                content.add(rndString());
            }
            content.add(new Button("b" + i, event -> {
                Button b = event.getButton();
                System.out.println(b.getCaption() + " click: " + new Date());
                System.out.println(b.getUI().getSession());
            }));
            t.addItem(content.toArray(), "" + i);
        }
        t.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        return t;
    }

    static String[] testString = { "Jacob", "Michael", "Joshua", "Matthew",
            "Ethan", "Andrew", "Daniel", "Anthony", "Christopher", "Joseph",
            "William", "Alexander", "Ryan", "David", "Nicholas", "Tyler",
            "James", "John", "Jonathan", "Nathan", "Samuel", "Christian",
            "Noah", "Dylan", "Benjamin", "Logan", "Brandon", "Gabriel",
            "Zachary", "Jose", "Elijah", "Angel", "Kevin", "Jack", "Caleb",
            "Justin", "Austin", "Evan", "Robert", "Thomas", "Luke", "Mason",
            "Aidan", "Jackson", "Isaiah", "Jordan", "Gavin", "Connor", "Aiden",
            "Isaac", "Jason", "Cameron", "Hunter", "Jayden", "Juan", "Charles",
            "Aaron", "Lucas", "Luis", "Owen", "Landon", "Diego", "Brian",
            "Adam", "Adrian", "Kyle", "Eric", "Ian", "Nathaniel", "Carlos",
            "Alex", "Bryan", "Jesus", "Julian", "Sean", "Carter", "Hayden",
            "Jeremiah", "Cole", "Brayden", "Wyatt", "Chase", "Steven",
            "Timothy", "Dominic", "Sebastian", "Xavier", "Jaden", "Jesse",
            "Devin", "Seth", "Antonio", "Richard", "Miguel", "Colin", "Cody",
            "Alejandro", "Caden", "Blake", "Carson" };

    public static String rndString() {
        return testString[(int) (Math.random() * testString.length)];
    }

}
