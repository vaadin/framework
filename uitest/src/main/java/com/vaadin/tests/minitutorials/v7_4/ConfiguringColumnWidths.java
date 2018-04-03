package com.vaadin.tests.minitutorials.v7_4;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionMode;

@SuppressWarnings("unused")
public class ConfiguringColumnWidths extends UI {

    @Override
    protected void init(VaadinRequest request) {
        Grid grid = new Grid(GridExampleHelper.createContainer());
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setColumnOrder("name", "amount", "count");

        setupCase(grid, 3);

        setContent(grid);
    }

    private void setupCase1(Grid grid) {
        grid.getColumn("name").setExpandRatio(1);
    }

    private void setupCase2(Grid grid) {
        grid.getColumn("name").setExpandRatio(1);
        grid.getColumn("amount").setWidth(100);
        grid.getColumn("count").setWidth(100);
    }

    private void setupCase3(Grid grid) {
        grid.setWidth("400px");
        grid.getColumn("name").setExpandRatio(1);
        grid.getColumn("amount").setWidth(100);
        grid.getColumn("count").setWidth(100);
    }

    private void setupCase4(Grid grid) {
        grid.setWidth("400px");
        grid.getColumn("name").setMinimumWidth(250);
        grid.getColumn("amount").setWidth(100);
        grid.getColumn("count").setWidth(100);
    }

    private void setupCase5(Grid grid) {
        grid.setWidth("400px");
        grid.setFrozenColumnCount(1);
        grid.getColumn("name").setMinimumWidth(250);
        grid.getColumn("amount").setWidth(100);
        grid.getColumn("count").setWidth(100);
    }

    private void setupCase6(Grid grid) {
        grid.setWidth("700px");
        grid.setFrozenColumnCount(1);
        grid.getColumn("name").setMinimumWidth(250);
        grid.getColumn("amount").setWidth(100);
        grid.getColumn("count").setWidth(100);
    }

    private void setupCase(Grid grid, int number) {
        if (number == 0) {
            return;
        }
        try {
            getClass().getDeclaredMethod("setupCase" + number, Grid.class)
                    .invoke(this, grid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
