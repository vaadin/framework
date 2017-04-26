package com.vaadin.tests.components.treegrid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.renderers.ComponentRenderer;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridNoHeaderOnInit extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TreeGrid<String> grid = new TreeGrid<>();
        grid.addColumn(Object::toString).setCaption("toString with Caption");
        grid.addColumn(t -> new Label(t), new ComponentRenderer());
        grid.setItems("Foo", "Bar", "Baz");
        grid.removeHeaderRow(0);
        grid.appendFooterRow();
        addComponent(grid);
    }
}
