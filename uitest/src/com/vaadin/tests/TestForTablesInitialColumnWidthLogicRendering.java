/* 
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tests;

import java.util.Iterator;
import java.util.Vector;

import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * This Component contains some simple test to see that component updates its
 * contents propertly.
 * 
 * @author Vaadin Ltd.
 */
public class TestForTablesInitialColumnWidthLogicRendering extends
        CustomComponent {

    private final VerticalLayout main = new VerticalLayout();

    public TestForTablesInitialColumnWidthLogicRendering() {

        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(new Label(
                "Below are same tables that all should render somewhat nice. Also when testing, you might want to try resizing window."));

        Table t;

        Layout lo = new VerticalLayout();
        lo.setWidth("600px");
        lo.setHeight("250px");

        t = getTestTable(4, 50);
        t.setSizeFull();
        lo.setCaption("Fullsize table insize 400x250px layout");
        lo.addComponent(t);
        main.addComponent(lo);

        // t = new Table("Empty table");
        // main.addComponent(t);

        t = getTestTable(5, 0);
        t.setCaption("Table with only headers");
        // main.addComponent(t);

        t = getTestTable(5, 200);
        t.setCaption("Table with  some cols and lot of rows");
        main.addComponent(t);

        t = getTestTable(5, 5);
        t.setCaption("Table with  some cols and rows rows, some col widths fixed");

        Iterator<?> it = t.getContainerPropertyIds().iterator();
        it.next();
        it.next();
        t.setColumnWidth(it.next(), 30);
        t.setColumnWidth(it.next(), 30);
        t.setWidth("700px");
        main.addComponent(t);

        t = getTestTable(12, 4);
        t.setCaption("Table with  some rows and lot of columns");
        main.addComponent(t);

        t = getTestTable(3, 40);
        t.setCaption("Table with some columns and wide explicit width. (Ought to widen columns to use all space)");
        t.setWidth("1000px");
        main.addComponent(t);

        t = getTestTable(12, 4);
        t.setCaption("Table with  some rows and lot of columns, width == 100%");
        t.setWidth(100, Table.UNITS_PERCENTAGE);
        main.addComponent(t);

        t = getTestTable(12, 100);
        t.setCaption("Table with  lot of rows and lot of columns, width == 50%");
        t.setWidth(50, Table.UNITS_PERCENTAGE);
        main.addComponent(t);

        t = getTestTable(5, 100);
        t.setCaption("Table with 40 rows");
        // main.addComponent(t);

        t = getTestTable(4, 4);
        t.setCaption("Table with some rows and width = 200px");

        t.setWidth("200px");
        main.addComponent(t);

        final Button b = new Button("refresh view", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                createNewView();
            }
        });
        main.addComponent(b);

    }

    public static Table getTestTable(int cols, int rows) {
        final Table t = new Table();
        t.setColumnCollapsingAllowed(true);
        for (int i = 0; i < cols; i++) {
            t.addContainerProperty(testString[i], String.class, "");
        }
        t.addContainerProperty("button", Button.class, null);
        for (int i = 0; i < rows; i++) {
            final Vector<Object> content = new Vector<Object>();
            for (int j = 0; j < cols; j++) {
                content.add(rndString());
            }
            Button button = new Button("b", new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    System.out.println("b click");

                }
            });
            button.setDescription("Yep yep");
            button.setComponentError(new UserError("Error"));
            content.add(button);
            t.addItem(content.toArray(), "" + i);
        }
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

    public static String rndString() {
        return testString[(int) (Math.random() * testString.length)];
    }

}
