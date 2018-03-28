package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

/**
 * Test UI verifying navigating in combobox via arrow keys.
 */
public class ComboBoxScrollingWithArrows extends AbstractReindeerTestUI {
    final String DESCRIPTION = "When positioned on last item in the page and press downArrow key - should open new page and set focus on the first item.";

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        addComponent(layout);
        addComboBox(layout);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return DESCRIPTION;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 11333;
    }

    private void addComboBox(AbstractLayout layout) {
        ComboBox<String> box = new ComboBox<>();
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add("item " + i);
        }
        box.setItems(items);
        box.setPageLength(10);
        box.setEmptySelectionAllowed(false);
        layout.addComponent(box);
    }
}
