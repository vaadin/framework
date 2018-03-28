package com.vaadin.tests.components.treetable;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TreeTable;

public class TreeTableRowGenerator extends AbstractReindeerTestUI {
    public static final String COLUMN_A = "first";
    public static final String COLUMN_B = "second";

    @Override
    protected void setup(VaadinRequest request) {
        TreeTable treeTable = new TreeTable();

        final HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
        hierarchicalContainer.addContainerProperty(COLUMN_A, String.class, "");
        hierarchicalContainer.addContainerProperty(COLUMN_B, String.class, "");

        Item it = hierarchicalContainer.addItem(0);
        it.getItemProperty(COLUMN_A).setValue("row 1 column a");
        it.getItemProperty(COLUMN_B).setValue("row 1 column b");
        hierarchicalContainer.setChildrenAllowed(0, true);

        Item it2 = hierarchicalContainer.addItem(1);
        it2.getItemProperty(COLUMN_A).setValue("row 2 column a");
        it2.getItemProperty(COLUMN_B).setValue("row 2 column b");
        hierarchicalContainer.setChildrenAllowed(1, false);

        hierarchicalContainer.setParent(1, 0);

        treeTable.setRowGenerator(new Table.RowGenerator() {
            @Override
            public Table.GeneratedRow generateRow(Table table, Object itemId) {
                if (table instanceof TreeTable
                        && ((TreeTable) table).areChildrenAllowed(itemId)) {
                    return new Table.GeneratedRow("Spanned Row");
                } else {
                    return null;
                }
            }
        });

        treeTable.setContainerDataSource(hierarchicalContainer);
        treeTable.setSizeFull();
        addComponent(treeTable);
    }
}
