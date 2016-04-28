package com.vaadin.tests.tickets;

import java.util.Collection;
import java.util.HashSet;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class Ticket3146 extends LegacyApplication {

    Table table;
    TextField result;

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("Test");

        table = new Table();
        table.addContainerProperty("Items", String.class, null);
        table.addItem(new String[] { "a" }, "a");
        table.addItem(new String[] { "b" }, "b");
        table.addItem(new String[] { "c" }, "c");
        for (int i = 1; i < 100; ++i) {
            table.addItem(new String[] { "Item " + i }, "Item " + i);
        }
        table.setMultiSelect(true);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setHeight("200px");
        table.setWidth("200px");
        mainWindow.addComponent(table);

        Button clearButton = new Button("Clear selection",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        clearSelection();
                    }
                });
        mainWindow.addComponent(clearButton);
        Button clearButton2 = new Button("Clear selection 2",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        clearSelection2();
                    }
                });
        mainWindow.addComponent(clearButton2);
        Button clearButton3 = new Button("Clear selection 3",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        clearSelection3();
                    }
                });
        mainWindow.addComponent(clearButton3);
        Button printButton = new Button("Print selection",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        printSelection();
                    }
                });
        mainWindow.addComponent(printButton);

        result = new TextField();
        result.setHeight("200px");
        result.setWidth("200px");
        mainWindow.addComponent(result);

        setMainWindow(mainWindow);
    }

    void clearSelection() {
        table.setValue(null);
    }

    void clearSelection2() {
        table.setValue(new HashSet<Object>());
    }

    void clearSelection3() {
        table.unselect("a");
        table.unselect("b");
        table.unselect("c");
    }

    void printSelection() {
        String selection = "";
        for (Object item : (Collection<?>) table.getValue()) {
            selection = selection + item + ' ';
        }
        result.setValue(selection);
    }

}
