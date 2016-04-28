package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabSheetIndexOperations extends TestBase {

    private int tabCounter = 1;

    @Override
    protected void setup() {
        final TabSheet tabs = new TabSheet();

        // Add some tabs
        tabs.addTab(new Label("Content 1"), "Tab 1", null);
        tabs.addTab(new Label("Content 2"), "Tab 2", null);
        tabs.addTab(new Label("Content 3"), "Tab 3", null);

        addComponent(tabs);

        Button addTab = new Button("Add tab at index 2",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        tabs.addTab(new Label("Content " + tabCounter),
                                "Added Tab " + tabCounter, null, 2);
                        tabCounter++;
                    }
                });
        addComponent(addTab);

        Button setCaption = new Button("Invert tab caption at index 2",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Tab tab = tabs.getTab(2);
                        tab.setCaption(new StringBuffer(tab.getCaption())
                                .reverse().toString());
                    }
                });
        addComponent(setCaption);

        Button move = new Button("Move selected tab to index 2",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        tabs.setTabPosition(tabs.getTab(tabs.getSelectedTab()),
                                2);
                    }
                });
        addComponent(move);

        Button getIndex = new Button("Get selected tab index",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getMainWindow().showNotification(
                                "Index: "
                                        + tabs.getTabPosition(tabs.getTab(tabs
                                                .getSelectedTab())));

                    }
                });
        addComponent(getIndex);
    }

    @Override
    protected String getDescription() {
        return "You can use indexes to add and reorder the TabSheet";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6188;
    }

}
