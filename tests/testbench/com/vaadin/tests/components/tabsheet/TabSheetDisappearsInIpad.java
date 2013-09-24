package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class TabSheetDisappearsInIpad extends TestBase {

    Table table = new Table("Table");
    TabSheet tabsheet = new TabSheet();

    @Override
    protected void setup() {

        table.setSizeFull();
        addComponent(table);

        tabsheet.addTab(new Label("Tab1"), "tab1");
        tabsheet.addTab(new Label("Tab2"), "tab2");
        tabsheet.addTab(new Label("Tab3"), "tab3");

        Window win = new Window();
        win.center();
        win.setContent(tabsheet);

        getMainWindow().addWindow(win);
    }

    @Override
    protected String getDescription() {
        return "In iOS5.1 subwindow content disappears in some cases if it contains a tabsheet and tab is changed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9424;
    }

}
