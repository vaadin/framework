package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class GridLayoutWithLabel extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout layout = new GridLayout(2, 1);
        layout.setSpacing(true);

        layout.addComponent(new Label("This is the label"), 0, 0);

        CheckBox cb = new CheckBox("Unchecked");
        cb.addValueChangeListener(evt -> {
            cb.setCaption(evt.getValue() ? "Checked" : "Unchecked");
        });

        layout.addComponent(cb, 1, 0);

        addComponent(layout);
    }
}
