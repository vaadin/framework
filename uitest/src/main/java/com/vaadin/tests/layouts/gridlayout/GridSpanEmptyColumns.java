package com.vaadin.tests.layouts.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class GridSpanEmptyColumns extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout gridLayout = new GridLayout(3, 1);
        gridLayout.setWidth("1000px");

        Label bigCell = new Label("big cell");
        bigCell.setId("bigCell");
        bigCell.setWidth("100%"); // Only to make test backwards compatible
        Label smallCell = new Label("small cell");
        smallCell.setId("smallCell");
        smallCell.setWidth("100%"); // Only to make test backwards compatible
        gridLayout.addComponent(bigCell, 0, 0, 1, 0); // spans first two columns
        gridLayout.addComponent(smallCell, 2, 0, 2, 0); // last column only

        addComponent(gridLayout);
    }

    @Override
    protected String getTestDescription() {
        return "A 3x1 grid has a spanned component on the first two cells and a component on the last cell. The two components should occupy 2/3 and 1/3 of the available space respectively, instead of 1/2 each.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14335;
    }
}
