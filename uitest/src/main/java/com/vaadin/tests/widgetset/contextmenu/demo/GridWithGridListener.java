package com.vaadin.tests.widgetset.contextmenu.demo;

import java.util.Collections;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.tests.widgetset.contextmenu.GridContextMenu;
import com.vaadin.ui.Grid;

public class GridWithGridListener extends Grid<String[]> {

    public GridWithGridListener() {
        setCaption("Grid with Grid specific listener");
        setHeightByRows(3);
        final GridContextMenu<String[]> gridContextMenu = new GridContextMenu<>(
                this);

        addColumn(arr -> arr[0]).setCaption("Column 1(right-click here)");
        addColumn(arr -> arr[1]).setCaption("Column 2(right-click here)");

        final ListDataProvider<String[]> dataSource = new ListDataProvider<>(
                Collections.singletonList(new String[] { "foo", "bar" }));
        setDataProvider(dataSource);
        gridContextMenu.addGridHeaderContextMenuListener(e -> {
            gridContextMenu.removeItems();
            gridContextMenu.addItem(getText(e), null);
        });

        gridContextMenu.addGridBodyContextMenuListener(e -> {
            gridContextMenu.removeItems();
            gridContextMenu.addItem(getText(e), null);
        });

    }

    private static String getText(
            final GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent<String[]> e) {

        final Column<String[], ?> column = e.getColumn();
        final String columnText;
        if (column != null) {
            columnText = "'" + column.getCaption() + "'";
        } else {
            columnText = "'?'";
        }
        final String gridCaption = e.getComponent() == null ? "(NULL)"
                : e.getComponent().getCaption();

        return "Context menu for " + e.getSection() + ", column: " + columnText
                + ", gridCaption: " + gridCaption;

    }

}
