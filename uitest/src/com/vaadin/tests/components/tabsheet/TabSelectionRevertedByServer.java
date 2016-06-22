package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

/**
 * TabSheet test in case user selects a tab and on the selection listener the
 * selected tab is changed to another one.
 * 
 * This test used to cause nonfunctional TabSheet if the current tab was 1, user
 * selects 5, then the selection listener will revert the selected tab to 1.
 * 
 * @author Vaadin Ltd
 */
public class TabSelectionRevertedByServer extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final TabSheet tabsheet = new TabSheet();
        tabsheet.setWidth("400px");

        Component lastLabel = null;

        for (int i = 1; i <= 5; i++) {
            String caption = "Tab " + i;
            Label label = new Label(caption);
            tabsheet.addTab(label, caption);

            lastLabel = label;
        }

        tabsheet.setSelectedTab(0);

        final Component lastTab = lastLabel;

        tabsheet.addSelectedTabChangeListener(new SelectedTabChangeListener() {

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if (tabsheet.getSelectedTab().equals(lastTab)) {

                    // Set focus back to first tab in tabsheet
                    tabsheet.setSelectedTab(0);
                    Notification.show("Focus set back to tab at position 0");
                }
            }
        });

        addComponent(tabsheet);

        addButton("Select Last Tab", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tabsheet.setSelectedTab(lastTab);
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Clicking on Tab 5 will revert to Tab 1. The action is handled on the server side and will set the selected tab to 1 if Tab 5 is selected.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14710;
    }

}
