package com.vaadin.tests.components.grid.basicfeatures;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.grid.GridHeightByRowOnInitWidget;
import com.vaadin.tests.widgetset.server.TestWidgetComponent;
import com.vaadin.ui.UI;

@Theme("valo")
@Title("Client Grid height by row on init")
@Widgetset(TestingWidgetSet.NAME)
public class GridClientHeightByRowOnInit extends UI {
    @Override
    protected void init(VaadinRequest request) {
        setContent(new TestWidgetComponent(GridHeightByRowOnInitWidget.class));
    }
}
