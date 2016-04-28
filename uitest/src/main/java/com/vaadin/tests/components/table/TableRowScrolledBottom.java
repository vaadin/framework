package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class TableRowScrolledBottom extends AbstractTestUI {

    final static String part1 = "This is a test item with long text so that there is something to see Nr. ";
    final static String part2 = ". This text must be long otherwise the timing issue on Firefox does not occur. This works fine in IE";

    @Override
    protected void setup(VaadinRequest request) {

        final Table table = new Table();
        table.setSizeFull();
        table.addContainerProperty("Test", Label.class, null);
        table.setHeight("100%");

        Button button = new Button("Add 100 items");
        button.addClickListener(new Button.ClickListener() {
            int i = 0;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                for (int j = 0; j < 100; j++) {
                    ++i;
                    table.addItem(new Object[] { new Label(part1 + "<b>" + i
                            + "</b>" + part2, ContentMode.HTML) }, i);
                    table.setCurrentPageFirstItemIndex(table
                            .getContainerDataSource().size() - 1);
                }
            }
        });

        addComponent(table);
        addComponent(button);
        getLayout().setExpandRatio(table, 1f);
    }

    @Override
    protected String getTestDescription() {
        return "Table should be scrolled to bottom when adding rows and updating currentPageFirstItemIndex to last item";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10970;
    }

}
