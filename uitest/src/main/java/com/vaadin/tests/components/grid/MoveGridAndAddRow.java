package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MoveGridAndAddRow extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();

        final VerticalLayout anotherLayout = new VerticalLayout();
        anotherLayout.addComponent(new Label("This is another layout"));
        final Grid g = new Grid();
        g.addColumn("A");
        g.addRow("1");

        final Button b = new Button("Add row and remove this button");
        b.setId("add");
        b.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent e) {
                g.addRow("2");
                b.setVisible(false);
            }
        });

        Button move = new Button("Move grid to other layout");
        move.setId("move");
        move.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                anotherLayout.addComponent(g);
            }
        });

        layout.addComponents(b, move, g);
        addComponent(new HorizontalLayout(layout, anotherLayout));

    }
}
