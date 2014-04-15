package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabSheetTabStyleNames extends TestBase {

    private static final String STYLE_NAME = "TabSheetTabStyleNames";

    @Override
    public void setup() {
        setTheme("tests-tickets");

        TabSheet tabsheet = new TabSheet();
        final Tab tab1 = tabsheet.addTab(new Label(), "Tab 1");
        final Tab tab2 = tabsheet.addTab(new Label(), "Tab 2");

        tab1.setStyleName(STYLE_NAME);

        addComponent(new Button("Update style names",
                new Button.ClickListener() {
                    int counter = 0;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (tab1.getStyleName() == null) {
                            tab1.setStyleName(STYLE_NAME);
                        } else {
                            tab1.setStyleName(null);
                        }

                        tab2.setStyleName(STYLE_NAME + "_" + (counter++));
                    }
                }));

        addComponent(tabsheet);
    }

    @Override
    protected String getDescription() {
        return "Tests setting style names for individual tabs.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5880;
    }
}
