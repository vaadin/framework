package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Table;

public class GridLayoutExpand extends GridBaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        buildLayout();
        super.setup(request);
        layout.setSizeFull();
    }

    private void buildLayout() {
        class ExpandButton extends Button {

            public ExpandButton(final int i1, final int i2, final float e1,
                    final float e2) {
                super();
                setCaption("Expand ratio: " + e1 * 100 + " /" + e2 * 100);
                addClickListener(event -> {
                    layout.setColumnExpandRatio(i1, e1);
                    layout.setColumnExpandRatio(i2, e2);
                });
            }
        }
        Table t1 = getTestTable();
        Table t2 = getTestTable();
        layout.setColumns(4);
        layout.setRows(4);
        layout.addComponent(new ExpandButton(1, 2, 1.0f, 0.0f), 0, 0);
        layout.addComponent(new ExpandButton(1, 2, 0.5f, 0.50f), 0, 1);
        layout.addComponent(new ExpandButton(1, 2, .25f, 0.75f), 0, 2);

        layout.addComponent(t1, 1, 1);
        layout.addComponent(t2, 2, 1);
    }
}
