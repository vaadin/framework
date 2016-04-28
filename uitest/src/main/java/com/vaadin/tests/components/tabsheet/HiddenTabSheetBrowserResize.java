package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class HiddenTabSheetBrowserResize extends TestBase {

    @Override
    public void setup() {
        final TabSheet tabSheet = new TabSheet();

        tabSheet.addTab(new Label("Label1"), "Tab1");
        tabSheet.addTab(new Label("Label2"), "Tab2");

        Button toggleButton = new Button("Toggle TabSheet",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        tabSheet.setVisible(!tabSheet.isVisible());
                    }
                });
        addComponent(toggleButton);
        addComponent(tabSheet);
    }

    @Override
    protected String getDescription() {
        return "TabSheet content disappears if browser window resized when the TabSheet is hidden";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9508;
    }

}
