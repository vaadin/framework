package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.ui.Accordion;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;

public class Ticket2103 extends Application {
    private Window mainWindow;

    @Override
    public void init() {
        mainWindow = new Window(getClass().getSimpleName());
        mainWindow.setLayout(new ExpandLayout());
        mainWindow.setSizeFull();
        mainWindow.getLayout().setSizeFull();

        MyTable table1 = new MyTable(4, "table1");
        table1.loadTable(100);
        MyTable table2 = new MyTable(4, "table2");
        table2.loadTable(100);

        MyAccordion a = new MyAccordion(new Component[] { table1, table2 },
                "FDSF");
        mainWindow.addComponent(a);
        setMainWindow(mainWindow);
        // mainWindow.addComponent(table1);

    }

    public class MyAccordion extends Accordion {

        public MyAccordion(Component[] components, String id) {
            this.setWidth("100%");
            this.setHeight("100%");
            setDebugId(id);
            for (int i = 0; i < components.length; i++) {
                this.addTab(components[i], components[i].getDebugId(), null);
            }
        }
    }

    public class MyTable extends Table {

        private Table table = this;
        private String[] columns;
        private ExpandLayout layout = new ExpandLayout();

        public MyTable(int columnNumber, String id) {
            setDebugId(id);
            setSizeFull();
            columns = initializeColumns(columnNumber);
            table.setWidth("100%");
            table.setHeight("100%");
            table.setColumnReorderingAllowed(true);
            table.setColumnCollapsingAllowed(true);
            table.setSelectable(true);
            table.setMultiSelect(false);
            table.setNullSelectionAllowed(false);
            // table.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
            table.setContainerDataSource(createContainer());
            layout.addComponent(table);
        }

        public void loadTable(int itemNumber) {
            table.removeAllItems();
            for (int j = 0; j < itemNumber; j++) {
                Item rowItem = table.addItem(j);
                if (rowItem != null) {
                    for (int i = 0; i < columns.length; i++) {
                        rowItem.getItemProperty(columns[i]).setValue(
                                "Value" + j);
                    }
                }
            }
        }

        private HierarchicalContainer createContainer() {
            final HierarchicalContainer c = new HierarchicalContainer();
            for (int i = 0; i < columns.length; i++) {
                c.addContainerProperty(columns[i], String.class, null);
            }
            return c;
        }

        private String[] initializeColumns(int number) {
            String[] columns = new String[number];
            for (int i = 0; i < number; i++) {
                columns[i] = "Column" + i;
            }
            return columns;
        }

    }

}
