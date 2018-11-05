package com.vaadin.tests.server.component.gridlayout;

import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;

public class PreconfiguredGridLayout extends GridLayout {
    public PreconfiguredGridLayout() {
        setRows(2);
        setColumns(2);

        addComponent(new Button("1-1"));
        addComponent(new Button("2-1"));
        addComponent(new Button("1-2"));
        addComponent(new Button("2-2"));
    }
}
