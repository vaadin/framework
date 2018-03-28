package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

/**
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class TableScrollAfterAddRow extends AbstractReindeerTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {

        final int totalRows = 100;

        final VerticalLayout layout = new VerticalLayout();

        final IndexedContainer datasource = new IndexedContainer();

        datasource.addContainerProperty("value", Integer.class, -1);
        for (int i = 0; i < totalRows; i++) {
            addRow(datasource);
        }

        final Table table = new Table();
        table.setContainerDataSource(datasource);
        layout.addComponent(table);
        addComponent(layout);

        final Label label = new Label("");
        layout.addComponent(label);

        NativeButton addRowButton = new NativeButton("Add row",
                event -> addRow(datasource));

        NativeButton jumpToLastRowButton = new NativeButton("Jump to last row",
                event -> jumpToLastRow(table));
        NativeButton jumpTo15thRowButton = new NativeButton("Jump to 15th row",
                event -> jumpToFifteenthRow(table));
        NativeButton jumpToFirstRowButton = new NativeButton(
                "Jump to first row", event -> jumpToFirstRow(table));

        NativeButton updateLabelButton = new NativeButton("UpdateLabel",
                event ->
                        label.setValue(Integer.toString(
                        table.getCurrentPageFirstItemIndex())));
        layout.addComponent(addRowButton);
        layout.addComponent(jumpToLastRowButton);
        layout.addComponent(jumpTo15thRowButton);
        layout.addComponent(jumpToFirstRowButton);
        layout.addComponent(updateLabelButton);
    }

    private void jumpToFifteenthRow(Table table) {
        table.setCurrentPageFirstItemIndex(14);
    }

    private void jumpToLastRow(Table table) {
        int visibleRows = table.getContainerDataSource().size();
        table.setCurrentPageFirstItemIndex(visibleRows - 1);
    }

    private void jumpToFirstRow(Table table) {
        table.setCurrentPageFirstItemIndex(0);
    }

    private void addRow(IndexedContainer datasource) {
        int rowNumber = datasource.size();
        Item row = datasource.addItem(rowNumber);
        row.getItemProperty("value").setValue(rowNumber);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return "";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 14147;
    }
}
