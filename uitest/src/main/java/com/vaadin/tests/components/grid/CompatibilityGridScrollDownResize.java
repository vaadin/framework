package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.minitutorials.v7_4.GridExampleHelper;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Grid;

public class CompatibilityGridScrollDownResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // container with at least 100 rows
        final Grid grid = new Grid(GridExampleHelper.createContainer());
        grid.setSizeFull();
        addComponent(grid);
        getLayout().setSizeFull();
        getLayout().setExpandRatio(grid, 2);
        ((VerticalLayout) getLayout().getParent()).setSizeFull();
    }

    @Override
    protected Integer getTicketNumber() {
        return 11893;
    };

    @Override
    protected String getTestDescription() {
        return "Scrolling all the way down, resizing the browser window smaller "
                + "so that one row gets completely hidden, and scrolling down "
                + "again should keep the row contents consistent and in expected "
                + "sequence.";
    }
}
