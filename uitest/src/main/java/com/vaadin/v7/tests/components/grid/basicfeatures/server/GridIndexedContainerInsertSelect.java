package com.vaadin.v7.tests.components.grid.basicfeatures.server;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.sort.Sort;
import com.vaadin.v7.data.util.AbstractInMemoryContainer;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.VerticalLayout;

@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class GridIndexedContainerInsertSelect extends AbstractReindeerTestUI {

    private static final String INFO_COLUMN_PATTERN = "InsertedIndex: %d ItemUid: %d SelectedRowIndex: %d SelectedItemUid: %s";
    private static final String COLUMN1 = "Column1";
    private static final String COLUMN2 = "Column2";
    private int currInsertionIndex = 4;
    private VerticalLayout layout;
    private Grid grid;
    private IndexedContainer container;

    protected void setup(VaadinRequest vaadinRequest) {
        layout = new VerticalLayout();
        layout.setSizeFull();

        initGrid();
        initData(container);
        selectFirstRow();

        CheckBox checkBox = new CheckBox("Select row after insert", true);

        Button.ClickListener clickListener = e -> {
            addNewItem();
            selectOnlyLastItemIfRequested(checkBox);
            currInsertionIndex++;
        };

        Button button = new Button("Add row after selected row!",
                clickListener);

        HorizontalLayout inputBox = new HorizontalLayout();

        inputBox.addComponents(checkBox, button);

        layout.addComponents(grid, inputBox);

        addComponent(layout);
    }

    private void initGrid() {
        grid = new Grid();
        grid.setSizeFull();
        container = new IndexedContainer();
        container.addContainerProperty(COLUMN1, String.class, null);
        container.addContainerProperty(COLUMN2, String.class, null);
        grid.setContainerDataSource(container);
    }

    private void selectFirstRow() {
        grid.select("1");
    }

    private void initData(IndexedContainer container) {
        createRowItem("1", "Item 1", "ItemCol2 1", container);
        createRowItem("2", "Item 2", "ItemCol2 2", container);
        createRowItem("3", "Item 3", "ItemCol2 3", container);
    }

    private void selectOnlyLastItemIfRequested(CheckBox checkBox) {
        if (checkBox.getValue()) {
            grid.select(currInsertionIndex + "");
        }
    }

    private void addNewItem() {
        final Object selectedRow = grid.getSelectedRow();
        int currentIndex = container.indexOfId(selectedRow);
        int insertIndex = currentIndex + 1;
        final Item thirdItem = container.addItemAt(insertIndex,
                currInsertionIndex + "");
        setRowData(thirdItem, "Item " + currInsertionIndex,
                getInfoColumnContent(selectedRow, currentIndex, insertIndex));
    }

    private void setRowData(Item thirdItem, String column1Data,
            String column2Data) {
        thirdItem.getItemProperty(COLUMN1).setValue(column1Data);
        thirdItem.getItemProperty(COLUMN2).setValue(column2Data);
    }

    private String getInfoColumnContent(Object selectedRow, int currentIndex,
            int insertIndex) {
        return String.format(INFO_COLUMN_PATTERN, insertIndex,
                currInsertionIndex, currentIndex, selectedRow);
    }

    private void createRowItem(String id, String column1, String column2,
            AbstractInMemoryContainer<Object, Object, Item> container) {
        Item firstRow = container.addItem(id);
        setRowData(firstRow, column1, column2);
    }

}
