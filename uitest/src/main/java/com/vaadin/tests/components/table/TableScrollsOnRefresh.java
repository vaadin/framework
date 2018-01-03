package com.vaadin.tests.components.table;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TableScrollsOnRefresh extends AbstractTestUIWithLog {
    private Table table = new Table(
            "scroll down table, so it loads next page, and then click 'refresh' button");
    private Button refresh = new Button("refresh");
    private BeanItemContainer<TableItem> container = new BeanItemContainer<TableItem>(
            TableItem.class);

    @Override
    protected void setup(VaadinRequest request) {
        refresh.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                table.refreshRowCache();
            }
        });
        table.setSizeFull();
        VerticalLayout vl = new VerticalLayout(refresh, table);
        vl.setExpandRatio(table, 1f);
        vl.setSizeFull();
        setContent(vl);
        table.setContainerDataSource(container);
        populateContainer();
        // table.refreshRowCache();

    }

    private void populateContainer() {
        List<TableItem> items = new ArrayList<TableItem>();
        for (int i = 0; i < 1000; i++) {
            items.add(new TableItem("Item " + Integer.toString(i),
                    "Item description " + Integer.toString(i)));

        }
        container.addAll(items);
    }

    public class TableItem {

        private String name;
        private String description;

        public TableItem(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }
}
