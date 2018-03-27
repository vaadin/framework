package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridScrollOuterLayoutAfterContents extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Integer> grid = new Grid<>();

        // create column and fill rows
        grid.addColumn(item -> "name" + item).setCaption("Name");
        grid.addColumn(item -> "content" + item).setCaption("Content");
        grid.setItems(IntStream.range(1, 21).boxed());

        // set height mode and height
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(10);

        VerticalLayout layout = new VerticalLayout(grid, new TextArea());
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setSizeUndefined();

        Panel panel = new Panel();
        panel.setContent(layout);
        panel.setHeight("200px");
        panel.setWidthUndefined();

        addComponent(panel);
    }

    @Override
    protected String getTestDescription() {
        return "Should be possible to scroll to the TextArea underneath the Grid even on mobile";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9477;
    }
}
