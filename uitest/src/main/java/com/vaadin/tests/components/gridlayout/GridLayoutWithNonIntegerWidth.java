package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class GridLayoutWithNonIntegerWidth extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Panel p1 = new Panel("Panel with GridLayout");
        GridLayout grid = new GridLayout(1, 1, new Label("A"));
        grid.setWidth(100, Unit.PERCENTAGE);
        p1.setContent(grid);
        p1.setWidth("354.390625px");

        Panel p2 = new Panel("Panel with HorizontalLayout");
        HorizontalLayout hl = new HorizontalLayout(new Label("A"));
        hl.setWidth(100, Unit.PERCENTAGE);
        p2.setContent(hl);
        p2.setWidth("354.390625px");

        setContent(new VerticalLayout(p1, p2));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Neither of the panels should contain scrollbars";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 11775;
    }
}
