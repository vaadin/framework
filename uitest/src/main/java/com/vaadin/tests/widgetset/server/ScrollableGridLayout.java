package com.vaadin.tests.widgetset.server;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

public class ScrollableGridLayout extends GridLayout {

    public ScrollableGridLayout() {
        super();
    }

    public ScrollableGridLayout(int columns, int rows, Component... children) {
        super(columns, rows, children);
    }

    public ScrollableGridLayout(int columns, int rows) {
        super(columns, rows);
    }
}
