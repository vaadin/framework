package com.vaadin.tests.server.component.menubar;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarIdsTest extends TestCase implements Command {

    private MenuItem lastSelectedItem;
    private MenuItem menuFile;
    private MenuItem menuEdit;
    private MenuItem menuEditCopy;
    private MenuItem menuEditCut;
    private MenuItem menuEditPaste;
    private MenuItem menuEditFind;
    private MenuItem menuFileOpen;
    private MenuItem menuFileSave;
    private MenuItem menuFileExit;
    private Set<MenuItem> menuItems = new HashSet<MenuItem>();

    private MenuBar menuBar;

    @Override
    public void setUp() {
        menuBar = new MenuBar();
        menuFile = menuBar.addItem("File", this);
        menuEdit = menuBar.addItem("Edit", this);
        menuEditCopy = menuEdit.addItem("Copy", this);
        menuEditCut = menuEdit.addItem("Cut", this);
        menuEditPaste = menuEdit.addItem("Paste", this);
        menuEdit.addSeparator();
        menuEditFind = menuEdit.addItem("Find...", this);
        menuFileOpen = menuFile.addItem("Open", this);
        menuFileSave = menuFile.addItem("Save", this);
        menuFile.addSeparator();
        menuFileExit = menuFile.addItem("Exit", this);

        menuItems.add(menuFile);
        menuItems.add(menuEdit);
        menuItems.add(menuEditCopy);
        menuItems.add(menuEditCut);
        menuItems.add(menuEditPaste);
        menuItems.add(menuEditFind);
        menuItems.add(menuFileOpen);
        menuItems.add(menuFileSave);
        menuItems.add(menuFileExit);
    }

    public void testMenubarIdUniqueness() {
        // Ids within a menubar must be unique
        assertUniqueIds(menuBar);

        menuBar.removeItem(menuFile);
        MenuItem file2 = menuBar.addItem("File2", this);
        MenuItem file3 = menuBar.addItem("File3", this);
        MenuItem file2sub = file2.addItem("File2 sub menu", this);
        menuItems.add(file2);
        menuItems.add(file2sub);
        menuItems.add(file3);

        assertUniqueIds(menuBar);
    }

    private static void assertUniqueIds(MenuBar menuBar) {

        Set<Object> ids = new HashSet<Object>();

        for (MenuItem item : menuBar.getItems()) {
            assertUniqueIds(ids, item);
        }
    }

    private static void assertUniqueIds(Set<Object> ids, MenuItem item) {
        int id = item.getId();
        System.out.println("Item " + item.getText() + ", id: " + id);
        assertFalse(ids.contains(id));
        ids.add(id);
        if (item.getChildren() != null) {
            for (MenuItem subItem : item.getChildren()) {
                assertUniqueIds(ids, subItem);
            }
        }
    }

    @Override
    public void menuSelected(MenuItem selectedItem) {
        assertNull("lastSelectedItem was not cleared before selecting an item",
                lastSelectedItem);

        lastSelectedItem = selectedItem;

    }
}
