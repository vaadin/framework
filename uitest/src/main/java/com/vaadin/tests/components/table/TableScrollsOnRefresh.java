package com.vaadin.tests.components.table;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

public class TableScrollsOnRefresh extends AbstractTestUI {
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
        addComponents(refresh, table);
        VerticalLayout vl = getLayout();
        vl.setExpandRatio(table, 1f);
        vl.setSizeFull();
        vl.getParent().setSizeFull();
        table.setContainerDataSource(container);
        populateContainer();
    }

    private void populateContainer() {
        List<TableItem> items = new ArrayList<TableItem>();
        for (int i = 0; i < 1000; i++) {
            items.add(new TableItem("Item " + Integer.toString(i),
                    "Item description " + Integer.toString(i)));

        }
        container.addAll(items);
    }

    @Override
    protected String getTestDescription() {
        return "Refreshing row cache shouldn't change scroll position.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8707;
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
