package com.vaadin.tests.components.tabsheet;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

/**
 * Test class for resizing a scrolled TabSheet.
 *
 * @author Vaadin Ltd
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class ScrolledTabSheetResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        populate(tabSheet);

        addComponent(tabSheet);
        addComponent(new Button("use reindeer", e -> {
            setTheme("reindeer");
        }));
    }

    protected void populate(TabSheet tabSheet) {
        for (int i = 0; i < 20; i++) {
            String caption = "Tab " + i;
            Label label = new Label(caption);

            Tab tab = tabSheet.addTab(label, caption);
            tab.setClosable(true);
        }
    }

    @Override
    protected String getTestDescription() {
        return "When tabs are scrolled to the end and the TabSheet is made bigger, more tabs should become visible.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 807;
    }

}
