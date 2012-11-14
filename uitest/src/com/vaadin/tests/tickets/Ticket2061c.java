package com.vaadin.tests.tickets;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class Ticket2061c extends LegacyApplication implements
        SelectedTabChangeListener {

    private LegacyWindow mainWindow;
    private Panel p;

    @Override
    public void init() {
        mainWindow = new LegacyWindow("Vaadin");
        mainWindow.setSizeFull();
        mainWindow.getContent().setSizeFull();
        setMainWindow(mainWindow);

        VerticalLayout ol = new VerticalLayout();
        ol.setWidth("200px");
        ol.setHeight("200px");

        VerticalLayout ol2 = new VerticalLayout();
        ol2.setSizeFull();

        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        p = new Panel("This is a panel", pl);
        p.setSizeFull();

        Label label1 = new Label("This is a table!");
        label1.setHeight("1500px");
        label1.setWidth("1500px");
        p.setScrollTop(50);

        pl.addComponent(label1);
        ol2.addComponent(p);
        ol.addComponent(ol2);

        Label a = new Label("abc123");
        a.setCaption("Label a");
        ol.setCaption("OL");
        Tabs tab = new Tabs(new Component[] { a, ol });
        tab.addListener(this);
        mainWindow.addComponent(tab);

    }

    public class MyTable extends CustomComponent implements ValueChangeListener {

        private Table table = new Table();
        private String[] columns;
        private VerticalLayout layout = new VerticalLayout();

        public MyTable(int columnNumber, String id) {
            setId(id);
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

        @Override
        public void valueChange(ValueChangeEvent event) {

        }

    }

    public class Tabs extends TabSheet {

        public Tabs(Component[] components) {
            this.setWidth("100%");
            // this.setHeight("100%");
            for (int i = 0; i < components.length; i++) {
                this.addTab(components[i], components[i].getId(), null);
            }
        }
    }

    public class MyAccordion extends Accordion {

        public MyAccordion(Component[] components, String id) {
            this.setWidth("100%");
            this.setHeight("100%");
            setId(id);
            for (int i = 0; i < components.length; i++) {
                this.addTab(components[i], components[i].getId(), null);
            }
        }
    }

    @Override
    public void selectedTabChange(SelectedTabChangeEvent event) {
        p.setScrollTop(10);

    }

}
