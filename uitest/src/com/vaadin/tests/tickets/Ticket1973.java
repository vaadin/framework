package com.vaadin.tests.tickets;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class Ticket1973 extends com.vaadin.server.LegacyApplication {

    LegacyWindow main = new LegacyWindow();
    Table table = new Table();

    @Override
    public void init() {
        setMainWindow(main);

        final IndexedContainer container1 = new IndexedContainer();
        container1.addContainerProperty("layout", Component.class, null);

        final IndexedContainer container2 = new IndexedContainer();
        container2.addContainerProperty("layout", Component.class, null);

        fill(container1, 100, "Testi 1 :");
        fill(container2, 100, "Testi 2 :");

        table.setContainerDataSource(container1);

        Button refreshTable = new Button("Change table container");
        refreshTable.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent e) {
                table.setContainerDataSource(container2);
                table.setContainerDataSource(container1);
            }
        });

        main.addComponent(table);
        main.addComponent(refreshTable);
    }

    public void fill(IndexedContainer container, int size, String prefix) {
        for (int i = 0; i < size; i++) {
            Item item = container.addItem(new Integer(i));
            VerticalLayout layout = new VerticalLayout();
            layout.addComponent(new Button(prefix + i));
            item.getItemProperty("layout").setValue(layout);
        }
    }
}
