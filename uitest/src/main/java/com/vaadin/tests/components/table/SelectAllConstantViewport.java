package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.v7.ui.Table;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class SelectAllConstantViewport extends AbstractTestUIWithLog {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {

        final Table table = new Table();
        table.addContainerProperty("", Integer.class, null);
        table.setSizeFull();
        table.setMultiSelect(true);
        table.setNullSelectionAllowed(true);
        table.setSelectable(true);

        CheckBox selectAllCheckbox = new CheckBox("Select All");
        selectAllCheckbox.addValueChangeListener(event -> {
            if (event.getValue()) {
                table.setValue(table.getItemIds());
            } else {
                table.setValue(null);
            }
        });

        for (int i = 0; i < 200; i++) {
            table.addItem(new Object[] { new Integer(i) }, new Integer(i));
        }

        table.setCurrentPageFirstItemIndex(185);

        final CssLayout layout = new CssLayout();
        layout.addComponent(selectAllCheckbox);
        layout.addComponent(table);
        layout.setSizeFull();
        addComponent(layout);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {

        return "The scroll position of a table with many items should remain constant if all items are selected.";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 13341;
    }

}
