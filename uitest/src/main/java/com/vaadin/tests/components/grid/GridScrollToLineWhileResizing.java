package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.VerticalSplitPanel;

public class GridScrollToLineWhileResizing extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final VerticalSplitPanel vsp = new VerticalSplitPanel();
        vsp.setWidth(500, Unit.PIXELS);
        vsp.setHeight(500, Unit.PIXELS);
        vsp.setSplitPosition(100, Unit.PERCENTAGE);
        addComponent(vsp);

        Grid<Integer> grid = new Grid<>();
        grid.addColumn(item -> "cell" + item);
        grid.setItems(IntStream.range(0, 100).boxed());
        grid.setSizeFull();

        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.addSelectionListener(event -> {
            vsp.setSplitPosition(50, Unit.PERCENTAGE);
            grid.scrollTo(event.getFirstSelectedItem().get());
        });

        vsp.setFirstComponent(grid);
    }

    @Override
    protected String getTestDescription() {
        return "Tests scrollToLine while moving SplitPanel split position to resize the Grid on the same round-trip.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
