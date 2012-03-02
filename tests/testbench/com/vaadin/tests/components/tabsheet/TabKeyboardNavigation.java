package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TabKeyboardNavigation extends TestBase {

    int index = 1;
    TabSheet ts = new TabSheet();

    @Override
    protected void setup() {
        ts.setWidth("500px");
        ts.setHeight("500px");
        for (int i = 0; i < 4; ++i) {
            addTab();
        }
        Button b = new Button("Add a tab", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                addTab();
            }
        });
        addComponent(b);
        addComponent(new TextField());
        addComponent(ts);
        addComponent(new TextField());
    }

    @Override
    protected String getDescription() {
        return "The tab bar should be focusable and arrow keys should switch tabs";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5100;
    }

    private Tab addTab() {
        Layout content = new VerticalLayout();
        content.addComponent(new Label("Tab " + index));
        content.addComponent(new TextField());
        Tab tab = ts.addTab(content, "Tab " + index++, null);
        tab.setClosable(true);
        return tab;
    }
}
