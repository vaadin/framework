package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class TabSheetMinimal extends TestBase {

    int index = 1;
    TabSheet ts = new TabSheet();

    @Override
    protected void setup() {
        ts.setStyleName("minimal");
        Button b = new Button("Add a tab", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                ts.addTab(new Label("" + index), "Tab " + index, null);
                index++;

            }
        });
        addComponent(ts);
        addComponent(b);
    }

    @Override
    protected String getDescription() {
        return "Adding tabs to a 'minimal' style TabSheet work properly even if the TabSheet is initially empty";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4227;
    }

}
