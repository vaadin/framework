package com.vaadin.tests.components.tabsheet;

import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class ScrolledTabSheetHiddenTabsResize extends ScrolledTabSheetResize {

    @Override
    protected void populate(TabSheet tabSheet) {
        for (int i = 0; i < 40; i++) {
            String caption = "Tab " + i;
            Label label = new Label(caption);

            Tab tab = tabSheet.addTab(label, caption);
            tab.setClosable(true);
            tab.setVisible(i % 2 != 0);
        }
    }
}
