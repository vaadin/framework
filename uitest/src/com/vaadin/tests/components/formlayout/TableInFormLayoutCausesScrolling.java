package com.vaadin.tests.components.formlayout;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class TableInFormLayoutCausesScrolling extends AbstractTestCase {

    @Override
    public void init() {
        // Window Initialization.
        final LegacyWindow window = new LegacyWindow("Main Window");
        setMainWindow(window);

        // FormLayout creation
        final FormLayout fl = new FormLayout();
        window.setContent(fl);

        // Add 20 TextField
        for (int i = 20; i-- > 0;) {
            fl.addComponent(new TextField());
        }

        // Add 1 selectable table with some items
        final Table table = new Table();
        table.setSelectable(true);
        table.addContainerProperty("item", String.class, "");
        for (int i = 50; i-- > 0;) {
            table.addItem(new String[] { "item" + i }, i);
        }
        window.addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Clicking in the Table should not cause the page to scroll";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7309;
    }
}
