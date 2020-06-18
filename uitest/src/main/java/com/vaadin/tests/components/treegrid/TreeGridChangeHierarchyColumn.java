package com.vaadin.tests.components.treegrid;

import com.vaadin.data.TreeData;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeGrid;

public class TreeGridChangeHierarchyColumn extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "TreeGrid in MultiSelect mode should take hiden columns into account when"
                + " rendering frozen columns after hierarchy-column reset.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12026;
    }

    @Override
    protected void setup(VaadinRequest request) {
        TreeGrid<String> treeGrid = new TreeGrid<>();
        treeGrid.setId("TreeGrid");

        for (int i = 0; i < 20; i++) {
            String columnId = String.valueOf(i);
            Grid.Column<String, Component> column = addColumn(treeGrid,
                    columnId);
            column.setCaption(columnId);
            column.setId(columnId);
        }

        TreeData<String> data = treeGrid.getTreeData();
        data.addItem(null, "child");
        data.addItem("child", "grandChild");

        treeGrid.setHierarchyColumn(treeGrid.getColumns().get(0));

        Button hideHierCol = new Button("Hide Hierarchy Column");
        hideHierCol.addClickListener(e -> {
            treeGrid.getHierarchyColumn().setHidden(true);
        });
        hideHierCol.setId("hideHierColButton");

        Button setHierCol = new Button("Set new Hierarchy Column");
        setHierCol.addClickListener(e -> {
            treeGrid.getColumns().stream().filter(column -> !column.isHidden())
                    .findFirst().ifPresent(col -> {
                        treeGrid.setHierarchyColumn(col.getId());
                    });
        });
        setHierCol.setId("setHierColButton");

        treeGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        treeGrid.setFrozenColumnCount(1);

        addComponents(treeGrid, hideHierCol, setHierCol);
    }

    private Grid.Column<String, Component> addColumn(Grid<String> grid,
            String columnId) {
        return grid.addComponentColumn(val -> {
            Label label = new Label(columnId);
            label.setWidth(50, Unit.PIXELS);
            return new CssLayout(label);
        });
    }

}
