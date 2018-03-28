package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

/**
 * For tables that are contained in a layout, a delayed column layouting should
 * not be visible (because it makes the column jump around).
 *
 * #15189
 *
 * @author Vaadin Ltd
 */
public class DelayedColumnLayouting extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);

        Button reset = new Button("Recreate layout with contained table");
        verticalLayout.addComponent(reset);
        reset.addClickListener(event -> fillLayout(layout));

        fillLayout(layout);

        verticalLayout.addComponent(layout);
        verticalLayout.setExpandRatio(layout, 1f);

        setContent(verticalLayout);
    }

    private void fillLayout(VerticalLayout layout) {
        layout.removeAllComponents();

        Table table = new Table();
        table.setSizeFull();
        table.addContainerProperty("First", String.class, "");
        table.addContainerProperty("This column jumps", String.class, "");

        layout.addComponent(table);
        layout.setExpandRatio(table, 1f);
    }
}
