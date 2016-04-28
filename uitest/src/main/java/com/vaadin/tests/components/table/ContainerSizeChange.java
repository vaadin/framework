package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

public class ContainerSizeChange extends TestBase {

    private Table table;
    private MyDataSource ds;

    @Override
    protected String getDescription() {
        return "A table should be able to handle a decrease in the size of the container. The original container here contains 50 items and the decrease button removes 10 of these. To reproduce the problem: Click 'Decrease size' two times to reduce size to 30 and scroll to the end (50). What should happen is the table should notice the container size has decreased and show the last items which now exists in the new container.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2862;
    }

    @Override
    protected void setup() {
        table = new Table("A table");
        ds = new MyDataSource();
        table.setContainerDataSource(ds);
        table.setPageLength(5);
        addComponent(table);

        Button b = new Button("Decrease size", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                ds.decreaseSize();
            }

        });

        addComponent(b);

        b = new Button("Increase size", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                ds.increaseSize();
            }

        });

        addComponent(b);

    }

}

class MyDataSource extends IndexedContainer {

    private int size = 0;

    public MyDataSource() {
        addContainerProperty("a", String.class, "");
        addContainerProperty("b", String.class, "");
        addContainerProperty("c", String.class, "");

        for (int i = 0; i < 100; i++) {
            Item item = addItem(String.valueOf(i));
            item.getItemProperty("a").setValue("a " + i);
            item.getItemProperty("b").setValue("b " + i);
            item.getItemProperty("c").setValue("c " + i);
        }
        size = 50;
    }

    public void increaseSize() {
        size += 10;

    }

    public void decreaseSize() {
        if (size > 10) {
            size -= 10;
        }

    }

    @Override
    public int size() {
        return size;
    }
}
