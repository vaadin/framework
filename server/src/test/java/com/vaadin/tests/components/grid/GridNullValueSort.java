package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.tests.util.MockUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.AbstractRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import org.junit.Before;
import org.junit.Test;

public class GridNullValueSort {


    private static AbstractRenderer<Integer, Boolean> booleanRenderer() {
        return new AbstractRenderer<Integer, Boolean>(Boolean.class) {
        };
    }

    private Grid<Integer> grid;

    @Test
    public void testNumbers() {
        grid.sort(grid.getColumn("int"), SortDirection.DESCENDING);
        performSort();
    }

    @Test
    public void testStrings() {
        grid.sort(grid.getColumn("String"));
        performSort();
    }

    @Test
    public void testBooleans() {
        grid.sort(grid.getColumn("Boolean"));
        performSort();
    }

    private void performSort() {
        grid.getDataCommunicator().beforeClientResponse(true);
    }

    @Before
    public void setup() {
        VaadinSession.setCurrent(null);
        grid = new Grid<Integer>() {
            @Override
            protected <T extends ClientRpc> T getRpcProxy(Class<T> rpcInterface) {
                return super.getRpcProxy(rpcInterface);
            }
        };
        grid.addColumn(i -> i, new NumberRenderer()).setId("int").setSortable(true);
        grid.addColumn(i -> i == null ? null : String.valueOf(i)).setId("String").setSortable(true);
        grid.addColumn(i -> i == null ? null : i == 1, booleanRenderer()).setId("Boolean").setSortable(true);
        grid.setItems(1, 2, 3, null, null, null);
        new MockUI().setContent(grid);
    }
//todo order check
}
