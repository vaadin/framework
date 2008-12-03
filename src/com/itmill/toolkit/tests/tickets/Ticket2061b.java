package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.ui.Accordion;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.TabSheet.SelectedTabChangeEvent;
import com.itmill.toolkit.ui.TabSheet.SelectedTabChangeListener;

public class Ticket2061b extends Application implements
        SelectedTabChangeListener {

    private Window mainWindow;
    private Panel p;

    @Override
    public void init() {
        mainWindow = new Window("Ticket 2061b");
        mainWindow.setSizeFull();
        OrderedLayout mainLayout = (OrderedLayout) mainWindow.getLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin(false);
        setMainWindow(mainWindow);

        SplitPanel sp = new SplitPanel();
        sp.setSizeFull();
        sp.setSplitPosition(20, SplitPanel.UNITS_PIXELS);

        p = new Panel("This is a panel");
        p.setSizeFull();
        Label label1 = new Label("This is a table!");
        label1.setHeight("1500px");
        label1.setWidth("1500px");
        p.addComponent(label1);
        p.setScrollTop(50);
        // MyTable table1 = new MyTable(24, "table1");
        // table1.loadTable(1000);

        // MyTable table2 = new MyTable(24, "table2");
        // table2.loadTable(1000);

        // MyTable table3 = new MyTable(24, "table3");
        // table3.loadTable(1000);

        // MyAccordion accordion = new MyAccordion(new Component[] { table1,
        // table2 }, "Test");

        Label a = new Label("abc123");
        TextField tf = new TextField("A large textfield");
        tf.setHeight("2500px");
        tf.setWidth("2500px");

        TabsAcc tab = new TabsAcc(new Component[] { p, a, tf });
        tab.addListener(this);

        mainLayout.addComponent(sp);
        sp.addComponent(new Label("C 1"));
        // sp.addComponent(new Label("C 2"));
        // sp.setHeight("100px");

        sp.addComponent(tab);
        // mainLayout.addComponent(new Label("Filler"));
        // mainLayout.addComponent(tab);
        // mainLayout.setExpandRatio(tab, 1.0f);
        // sp.addComponent(new Label("Filler"));
        // sp.addComponent(tab);

        p = new Panel("This is a panel");
        p.setWidth("2000px");
        p.setHeight("2000px");
        Panel p2 = new Panel("This is another panel");
        p2.setWidth("2500px");
        p2.setHeight("2500px");
        label1 = new Label("This is a table!");
        label1.setHeight("1500px");
        label1.setWidth("1500px");
        p2.addComponent(label1);
        p.addComponent(p2);

        tab.addTab(p, "Panel with panel", null);
    }

    public class MyTable extends CustomComponent implements ValueChangeListener {

        private Table table = new Table();
        private String[] columns;
        private ExpandLayout layout = new ExpandLayout();

        public MyTable(int columnNumber, String id) {
            setDebugId(id);
            setCompositionRoot(layout);
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
            table.addListener(this);
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

        public void valueChange(ValueChangeEvent event) {

        }

    }

    public class Tabs extends TabSheet {

        public Tabs(Component[] components) {
            this.setWidth("100%");
            this.setHeight("100%");
            for (int i = 0; i < components.length; i++) {
                this.addTab(components[i], components[i].getDebugId(), null);
            }

        }

    }

    public class TabsAcc extends Accordion {

        public TabsAcc(Component[] components) {
            this.setWidth("100%");
            this.setHeight("100%");
            for (int i = 0; i < components.length; i++) {
                this.addTab(components[i], components[i].getDebugId(), null);
            }

        }

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

    public void selectedTabChange(SelectedTabChangeEvent event) {
        p.setScrollTop(10);

    }

}
