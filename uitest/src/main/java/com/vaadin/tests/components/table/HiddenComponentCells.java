package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;

public class HiddenComponentCells extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Table tbl = new Table();
        tbl.addContainerProperty("col1", Label.class, null);
        tbl.addContainerProperty("col2", Label.class, null);

        for (int rows = 0; rows < 20; rows++) {

            Item item = tbl.addItem(rows);

            Label cb = new Label("col1");
            cb.setVisible(rows % 2 == 0);
            item.getItemProperty("col1").setValue(cb);

            cb = new Label("col2");
            cb.setVisible((rows + 1) % 2 == 0);
            item.getItemProperty("col2").setValue(cb);
        }

        addComponent(tbl);
    }

    @Override
    protected String getTestDescription() {
        return "Hiding a component in the Table should not cause exceptions. <br/> Every other cell in the table should be hidden.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12119;
    }

}
