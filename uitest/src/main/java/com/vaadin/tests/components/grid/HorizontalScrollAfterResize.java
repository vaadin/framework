package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

/**
 * @author Vaadin Ltd
 *
 */
public class HorizontalScrollAfterResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<Integer> grid = new Grid<>();
        grid.setWidth("100%");
        grid.setHeight("350px");
        grid.setCaption("My Grid");

        for (int i = 0; i < 10; i++) {
            char ch = (char) ('a' + i);
            grid.addColumn(item -> "test").setCaption("" + ch);
        }

        grid.setItems(IntStream.of(0, 100).mapToObj(Integer::valueOf));

        addComponents(grid);
    }

    @Override
    protected String getTestDescription() {
        return "Don't add more than one scroll handler";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19189; // also 20254, 19622
    }

}
