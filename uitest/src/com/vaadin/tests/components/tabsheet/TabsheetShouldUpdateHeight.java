package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TabsheetShouldUpdateHeight extends TestBase {

    @Override
    public void setup() {
        final TabSheet tabsOuter = new TabSheet();
        final TabSheet tabsInner = new TabSheet();

        final Component tab2;

        tabsInner.addTab(tab2 = getLayoutWithComponents(6, "tab2"), "Tab 2");
        tabsInner.addTab(getLayoutWithComponents(8, "tab3"), "Tab 3");

        tabsOuter.addTab(tabsInner, "Inner tabs");
        tabsOuter.addTab(getLayoutWithComponents(10, "tab1"), "Tab 1");

        final Button btnSwitch = new Button("switch to Tab2",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(final ClickEvent inEvent) {
                        tabsOuter.setSelectedTab(tabsInner);
                        tabsInner.setSelectedTab(tab2);
                    }
                });

        addComponent(tabsOuter);
        addComponent(btnSwitch);
    }

    private VerticalLayout getLayoutWithComponents(final int inAmount, String id) {
        final VerticalLayout v = new VerticalLayout();
        v.setDebugId(id);
        v.setSpacing(true);
        v.setMargin(true);
        for (int i = 0; i < inAmount; i++) {
            v.addComponent(new TextField("Text field:"));
        }
        return v;
    }

    @Override
    protected String getDescription() {
        return "click with mouse first on tab 3 and then on tab 1. now click on the button 'switch to tab2'. then click on tab 3 again and the scrollbars appear";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9275;
    }
}
