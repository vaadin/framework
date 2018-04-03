package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

/**
 * In case a selected tab is removed the new selected one should be a neighbor.
 *
 * In case an unselected tab is removed and the selected one is not visible, the
 * scroll should not jump to the selected one.
 *
 * @since
 * @author Vaadin Ltd
 */
public class NewSelectionAfterTabRemove extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();

        for (int i = 0; i < 20; i++) {

            String caption = "Tab " + i;
            Label label = new Label(caption);

            Tab tab = tabSheet.addTab(label, caption);
            tab.setClosable(true);
        }

        addComponent(tabSheet);
    }

    @Override
    protected String getTestDescription() {
        return "When a selected tab is removed, its neighbor should become selected.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6876;
    }

}
