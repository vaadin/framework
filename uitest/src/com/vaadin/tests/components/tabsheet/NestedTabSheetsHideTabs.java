package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class NestedTabSheetsHideTabs extends TestBase {

    TabSheet main;
    TabSheet sub;

    @Override
    public void setup() {
        addComponent(new Button("Toggle tabs", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                main.hideTabs(!main.areTabsHidden());
            }
        }));
        sub = new TabSheet();
        sub.addTab(newPage(21), "Page 21");
        sub.addTab(newPage(22), "Page 22");
        main = new TabSheet();
        main.addTab(newPage(1), "Page 1");
        main.addTab(sub, "Page 2 (TabSheet)");
        main.addTab(newPage(3), "Page 3");
        addComponent(main);
    }

    private static ComponentContainer newPage(final int number) {
        final VerticalLayout vl = new VerticalLayout();
        vl.addComponent(new Label("Page " + number));
        return vl;
    }

    @Override
    protected String getDescription() {
        return "Setting hideTabs(true) for a TabSheet containing another TabSheet hides the nested TabSheet's tabs as well";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9294;
    }

}
