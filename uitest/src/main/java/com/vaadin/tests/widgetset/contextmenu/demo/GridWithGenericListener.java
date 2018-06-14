package com.vaadin.tests.widgetset.contextmenu.demo;

import java.util.Collections;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.tests.widgetset.contextmenu.ContextMenu;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;

public class GridWithGenericListener extends Grid<String[]> {

    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String SHOE_SIZE = "Shoe size";

    public GridWithGenericListener() {
        setCaption("Grid with generic listener");

        ContextMenu contextMenu2 = new ContextMenu(this, true);
        setHeightByRows(3);
        addColumn(arr -> arr[0]).setCaption(FIRST_NAME);
        addColumn(arr -> arr[1]).setCaption(LAST_NAME);
        addColumn(arr -> arr[2]).setCaption(SHOE_SIZE);

        ListDataProvider<String[]> ds = new ListDataProvider<>(Collections
                .singletonList(new String[] { "John", "Doe", "57" }));
        setDataProvider(ds);
        contextMenu2.addContextMenuOpenListener(this::onGenericContextMenu);
    }

    private void onGenericContextMenu(
            ContextMenu.ContextMenuOpenListener.ContextMenuOpenEvent event) {
        GridContextClickEvent<String[]> gridE = (GridContextClickEvent<String[]>) event
                .getContextClickEvent();
        ContextMenu menu = event.getContextMenu();
        menu.removeItems();
        if (gridE.getColumn() != null) {
            menu.addItem(
                    "Called from column " + gridE.getColumn().getCaption()
                            + " on row " + gridE.getRowIndex(),
                    f -> Notification.show("did something"));
        } else if (gridE.getRowIndex() >= 0) {
            menu.addItem("Called on row " + gridE.getRowIndex(),
                    f -> Notification.show("did something"));
        } else {
            menu.addItem("Called on background",
                    f -> Notification.show("did something"));

        }
    }

}
