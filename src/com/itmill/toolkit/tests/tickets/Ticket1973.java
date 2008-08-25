package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;

public class Ticket1973 extends com.itmill.toolkit.Application {

    Window main = new Window();
    Table table = new Table();

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
            OrderedLayout layout = new OrderedLayout();
            layout.addComponent(new Button(prefix + i));
            item.getItemProperty("layout").setValue(layout);
        }
    }
}