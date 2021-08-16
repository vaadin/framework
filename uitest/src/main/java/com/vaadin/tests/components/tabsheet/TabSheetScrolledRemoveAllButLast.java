package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class TabSheetScrolledRemoveAllButLast extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final TabSheet tabSheet = new TabSheet();
        tabSheet.setWidth("300px");

        for (int i = 0; i < 10; i++) {
            String caption = "Tab #" + (i + 1);
            tabSheet.addTab(new Label(caption), caption);
        }
        // scroll
        tabSheet.setSelectedTab(5);

        addComponent(tabSheet);

        Button button = new Button("Close all except last");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                int tabsCount = tabSheet.getComponentCount();
                for (int i = 0; i < tabsCount - 1; i++) {
                    TabSheet.Tab tab = tabSheet.getTab(0);
                    tabSheet.removeTab(tab);
                }
            }
        });
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Closing tabs shouldn't cause a client-side exception.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11673;
    }
}
