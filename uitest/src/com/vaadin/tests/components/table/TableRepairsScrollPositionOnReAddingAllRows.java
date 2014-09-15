package com.vaadin.tests.components.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;

public class TableRepairsScrollPositionOnReAddingAllRows extends AbstractTestUI {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setup(VaadinRequest request) {
        final BeanItemContainer<TableItem> cont = new BeanItemContainer<TableItem>(
                TableItem.class);
        final List<TableItem> itemList = new ArrayList<TableItem>();

        Button button1 = new Button("ReAdd rows");
        button1.setId("button1");
        button1.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                cont.removeAllItems();
                cont.addAll(itemList);
            }
        });

        for (int i = 0; i < 80; i++) {
            TableItem ti = new TableItem();
            ti.setName("Name_" + i);
            itemList.add(ti);
            cont.addBean(ti);
        }

        final Table table = new Table();
        table.setPageLength(-1);
        table.setContainerDataSource(cont);
        table.setSelectable(true);

        getLayout().addComponent(button1);
        getLayout().addComponent(table);
    }

    public class TableItem implements Serializable {
        private static final long serialVersionUID = -745849615488792221L;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    protected Integer getTicketNumber() {
        return 14581;
    }

    @Override
    protected String getTestDescription() {
        return "The scroll position should not be changed if removing and re-adding all rows in Table.";
    }
}
