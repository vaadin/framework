package com.vaadin.tests.components.table;

import java.io.Serializable;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class MissingScrollbar extends TestBase {

    private Table table;
    private IndexedContainer container50;
    private IndexedContainer container2;

    @Override
    protected String getDescription() {
        return "Increasing the number of items to more than is displayed at once should show the scrollbar.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3076;
    }

    @Override
    protected void setup() {
        HorizontalLayout hl = new HorizontalLayout();

        container50 = createContainer(50);
        container2 = createContainer(2);

        table = new Table();
        table.setContainerDataSource(container2);
        table.setPageLength(10);

        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setWidth(null);

        Button b = new Button("Set items to 2", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                table.setContainerDataSource(container2);
            }
        });
        buttonLayout.addComponent(b);

        b = new Button("Set items to 50", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                table.setContainerDataSource(container50);
            }

        });
        buttonLayout.addComponent(b);

        hl.addComponent(buttonLayout);
        hl.addComponent(table);

        addComponent(hl);
    }

    private static IndexedContainer createContainer(int items) {
        IndexedContainer ic = new IndexedContainer();
        ic.addContainerProperty("License number", Integer.class, "");
        ic.addContainerProperty("First", String.class, "");
        ic.addContainerProperty("Last", String.class, "");

        for (int i = 0; i < items; i++) {
            Item item = ic.addItem("" + i);
            item.getItemProperty("License number").setValue(i);
            item.getItemProperty("First").setValue("First " + i);
            item.getItemProperty("Last").setValue("Last " + i);
        }

        return ic;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws Exception {

        out.defaultWriteObject();
        System.out.println("Serialize " + getClass().getName() + "("
                + (this instanceof Serializable) + ")");
    }
}
