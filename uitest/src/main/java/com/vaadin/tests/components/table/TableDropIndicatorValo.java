package com.vaadin.tests.components.table;

import com.vaadin.annotations.Theme;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Table;

@Theme("valo")
@SuppressWarnings("serial")
public class TableDropIndicatorValo extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        Table table = new Table();

        table.addContainerProperty("foo", Integer.class, 0);
        table.addContainerProperty("bar", Integer.class, 0);
        // table.addContainerProperty("button", Button.class, null);

        for (int i = 0; i < 40; i++) {
            // Button b = new Button("testbutton");
            // b.setHeight("50px");
            table.addItem(new Object[] { i, i }, i);
        }

        table.setDragMode(Table.TableDragMode.ROW);
        table.setSelectable(true);

        table.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent dragAndDropEvent) {

            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }
        });

        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "Tests if the drop indicator appears between two rows as it should";
    }
}