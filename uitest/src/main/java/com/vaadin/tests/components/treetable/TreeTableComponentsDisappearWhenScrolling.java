package com.vaadin.tests.components.treetable;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.TreeTable;

public class TreeTableComponentsDisappearWhenScrolling extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label(getDescription(), ContentMode.HTML);
        addComponent(label);

        TreeTable table = new TreeTable();
        table.setSizeFull();
        table.setCacheRate(1.1);
        table.addContainerProperty(0, Button.class, null);
        for (int i = 0; i < 100; i++) {
            table.addItem(new Object[] { new Button(Integer.toString(i)) }, i);
            if ((i + 1) % 5 == 0) {
                for (int j = 0; j < 4; j++) {
                    table.setParent(i - j, i - 4);
                }
            }
        }

        addComponent(table);
    }

    @Override
    public String getDescription() {
        return "Expand nodes and scroll all the way down, then slowly back up"
                + ". More often than"
                + " not this results in 'flattening' of several rows. This is "
                + "due to component connectors being unregistered on "
                + "components, which are on visible table rows.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7964;
    }

}
