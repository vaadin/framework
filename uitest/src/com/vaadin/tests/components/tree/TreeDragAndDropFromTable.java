package com.vaadin.tests.components.tree;

import com.vaadin.data.Container;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TargetItemAllowsChildren;

public class TreeDragAndDropFromTable extends TestBase {

    @Override
    protected void setup() {

        HorizontalLayout h = new HorizontalLayout();
        addComponent(h);

        Table table = new Table();
        table.addContainerProperty("Column 1", String.class, "Row");
        table.setDragMode(TableDragMode.ROW);

        table.addItem("Row 1");
        table.addItem("Row 2");
        table.addItem("Row 3");
        table.addItem("Row 4");
        table.addItem("Row 5");
        table.addItem("Row 6");
        table.addItem("Row 7");

        h.addComponent(table);

        final Tree tree = new Tree();
        tree.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return TargetItemAllowsChildren.get();
            }

            @Override
            public void drop(DragAndDropEvent dropEvent) {
                // criteria verify that this is safe
                DataBoundTransferable t = (DataBoundTransferable) dropEvent
                        .getTransferable();
                Container sourceContainer = t.getSourceContainer();
                Object sourceItemId = t.getItemId();
                System.out.println(sourceItemId);

                AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails) dropEvent
                        .getTargetDetails());
                Object targetItemId = dropData.getItemIdOver();

                // move item from table to category'
                tree.addItem(sourceItemId);
                tree.setParent(sourceItemId, targetItemId);
                tree.setChildrenAllowed(sourceItemId, false);
                sourceContainer.removeItem(sourceItemId);

            }
        });

        tree.addItem("Item 1");
        tree.addItem("Item 11");
        tree.setChildrenAllowed("Item 11", false);
        tree.setParent("Item 11", "Item 1");
        tree.addItem("Item 12");
        tree.setChildrenAllowed("Item 12", false);
        tree.setParent("Item 12", "Item 1");
        tree.addItem("Item 13");
        tree.setChildrenAllowed("Item 13", false);
        tree.setParent("Item 13", "Item 1");

        tree.addItem("Item 2");
        tree.addItem("Item 21");
        tree.setChildrenAllowed("Item 21", false);
        tree.setParent("Item 21", "Item 2");
        tree.addItem("Item 22");
        tree.setChildrenAllowed("Item 22", false);
        tree.setParent("Item 22", "Item 2");
        tree.addItem("Item 23");
        tree.setChildrenAllowed("Item 23", false);
        tree.setParent("Item 23", "Item 2");

        tree.addItem("Item 3");
        tree.addItem("Item 31");
        tree.setChildrenAllowed("Item 31", false);
        tree.setParent("Item 31", "Item 3");
        tree.addItem("Item 32");
        tree.setChildrenAllowed("Item 32", false);
        tree.setParent("Item 32", "Item 3");
        tree.addItem("Item 33");
        tree.setChildrenAllowed("Item 33", false);
        tree.setParent("Item 33", "Item 3");

        tree.expandItemsRecursively("Item 1");
        tree.expandItemsRecursively("Item 2");
        tree.expandItemsRecursively("Item 3");

        h.addComponent(tree);
    }

    @Override
    protected String getDescription() {
        return "Test that childred can be dragged "
                + "from the Table to the tree and that TargetItemAllowsChildren limits "
                + "the drops to nodes which allows children";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7687;
    }

}
