package com.vaadin.tests.dd;

import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.Or;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;

/**
 * DD playground. Better quality example/prototype codes in {@link DDTest2}.
 */
public class DDTest8 extends TestBase {

    @Override
    protected void setup() {
        final Tree t = new Tree(
                "Tree with criteria from AbstractSelect (OverItem, ContainsItem). Foo can be dragged anywhere, anything can be dropped on Foo or Bar. Bar5 subtree is also valid drop target.");

        final HierarchicalContainer idx = new HierarchicalContainer();
        t.setContainerDataSource(idx);
        t.addItem("Foo");
        t.addItem("Bar");
        t.addItem("Bar1");
        t.addItem("Bar2");
        t.addItem("Bar3");
        t.addItem("Bar4");
        t.addItem("Bar5");
        t.addItem("Child");
        t.setParent("Child", "Foo");
        t.setSizeFull();
        t.setDragMode(TreeDragMode.NODE);

        /*
         * Moves items in tree (and could work in Table too). Also supports
         * "building" tree.
         * 
         * TODO fix algorithm, broken in some cases.
         */
        DropHandler itemSorter = new DropHandler() {

            @SuppressWarnings("unused")
            private void populateSubTree(HierarchicalContainer idx,
                    HierarchicalContainer subtree, Object itemId) {
                Collection<?> children = subtree.getChildren(itemId);
                if (children != null) {

                    for (Object childId : children) {
                        Item addItem = idx.addItem(childId);
                        if (addItem != null) {
                            // did not exist, populate properties
                            Item item = subtree.getItem(itemId);
                            Collection<?> itemPropertyIds = item
                                    .getItemPropertyIds();
                            for (Object propId : itemPropertyIds) {
                                addItem.getItemProperty(propId)
                                        .setValue(
                                                item.getItemProperty(propId)
                                                        .getValue());
                            }
                        }
                        idx.setParent(childId, itemId);
                        populateSubTree(idx, subtree, childId);
                    }
                }

            }

            @SuppressWarnings("unused")
            private HierarchicalContainer getSubTree(HierarchicalContainer idx,
                    Object itemId) {
                HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
                Collection<?> containerPropertyIds = idx
                        .getContainerPropertyIds();
                for (Object object : containerPropertyIds) {
                    hierarchicalContainer.addContainerProperty(object,
                            idx.getType(object), null);
                }
                hierarchicalContainer.addItem(itemId);
                copyChildren(idx, hierarchicalContainer, itemId);
                return hierarchicalContainer;
            }

            private void copyChildren(HierarchicalContainer source,
                    HierarchicalContainer target, Object itemId) {
                Collection<?> children = source.getChildren(itemId);
                if (children != null) {
                    for (Object childId : children) {
                        Item item = source.getItem(childId);
                        Item addedItem = target.addItem(childId);
                        target.setParent(childId, itemId);
                        Collection<?> itemPropertyIds = item
                                .getItemPropertyIds();
                        for (Object propertyId : itemPropertyIds) {
                            addedItem.getItemProperty(propertyId)
                                    .setValue(
                                            item.getItemProperty(propertyId)
                                                    .getValue());
                        }
                        copyChildren(source, target, childId);
                    }
                }

            }

            @Override
            public void drop(DragAndDropEvent event) {
                TreeTargetDetails details = (TreeTargetDetails) event
                        .getTargetDetails();
                // TODO set properties, so same sorter could be used in Table
                Transferable transferable = event.getTransferable();
                if (transferable instanceof DataBoundTransferable) {
                    DataBoundTransferable transferrable2 = (DataBoundTransferable) transferable;

                    Object itemId = transferrable2.getItemId();

                    Object itemIdOver = details.getItemIdOver();

                    // TODO could use the "folder" node id to make the drop
                    // logic simpler
                    Object itemIdInto = details.getItemIdInto();
                    VerticalDropLocation dropLocation = details
                            .getDropLocation();

                    Object itemIdAfter = details.getItemIdAfter();

                    if (itemIdOver.equals(itemIdInto)) { // directly on a node
                        t.setParent(itemId, itemIdOver);
                        return;
                    }

                    idx.setParent(itemId, itemIdInto);

                    if (dropLocation == null) {
                        System.err.println("No detail of drop place available");
                    }
                    idx.moveAfterSibling(itemId, itemIdAfter);
                }

                return;
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return new Or(new AbstractSelect.TargetItemIs(t, "Foo", "Bar"),
                        new AbstractSelect.AcceptItem(t, "Foo"),
                        t.new TargetInSubtree("Bar5") //
                );
            }

        };

        t.setDropHandler(itemSorter);

        getLayout().setSizeFull();
        addComponent(t);

    }

    @Override
    protected String getDescription() {
        return "Random DD tests";
    }

    @Override
    protected Integer getTicketNumber() {
        return 119;
    }

}
