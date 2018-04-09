package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.grid.GridCellFocusOnResetSizeWidget;
import com.vaadin.tests.widgetset.server.TestWidgetComponent;

@Widgetset(TestingWidgetSet.NAME)
public class GridCellFocusOnResetSize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(
                new TestWidgetComponent(GridCellFocusOnResetSizeWidget.class));
    }

}
