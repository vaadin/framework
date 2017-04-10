package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

public class GridClientMemoryLeak extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout l = new VerticalLayout();
        Button btn = new Button("newGrid");
        btn.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                l.removeComponent(l.getComponent(1));
                l.addComponent(grid());
            }
        });
        l.addComponent(btn);
        l.addComponent(grid());
        setContent(l);
        return;
    }

    private Grid grid() {
        Grid grid = new Grid();
        grid.addColumn("col1", String.class);
        grid.addColumn("col2", String.class);
        grid.addRow("a", "b" + System.currentTimeMillis());
        grid.addRow("d", "e");
        return grid;
    }
}
