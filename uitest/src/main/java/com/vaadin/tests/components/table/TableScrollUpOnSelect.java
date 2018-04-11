package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

/**
 * Test to see if Table appears to scroll up under an obscure set of conditions
 * (Scrolled down, set to expand, selecting updates a TextField that precedes
 * the Table in a VerticalLayout.) (#10106)
 *
 * @author Vaadin Ltd
 */
public class TableScrollUpOnSelect extends AbstractReindeerTestUI {
    public TextField text = null;

    @Override
    protected void setup(VaadinRequest request) {
        text = new TextField();
        text.setImmediate(true);

        final Table table = new Table(null);
        table.addContainerProperty("value", Integer.class, 0);
        for (int i = 0; i < 50; ++i) {
            table.addItem(new Object[] { i }, i);
        }
        table.setSizeFull();
        table.setSelectable(true);
        table.setImmediate(true);
        table.setEditable(false);

        final VerticalLayout layout = new VerticalLayout();

        table.addValueChangeListener(event -> {
            if (table.getValue() != null) {
                text.setValue(table.getValue().toString());
            }
        });

        table.setCurrentPageFirstItemIndex(49);

        layout.setSizeFull();
        layout.addComponent(text);
        layout.addComponent(table);
        layout.setExpandRatio(table, 1.0f);
        Window window = new Window();
        window.setHeight("600px");
        window.setWidth("400px");
        window.setModal(true);
        window.setContent(layout);
        getUI().addWindow(window);
    }

    @Override
    protected String getTestDescription() {
        return "Table scrolls up when selecting a row";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13358;
    }

}
