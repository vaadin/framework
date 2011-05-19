package com.vaadin.tests.components.tree;

import java.util.Collection;
import java.util.Date;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.AbstractSelect.AcceptItem;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.themes.Reindeer;

public class TreeWithConnectors extends TestBase {

    @Override
    protected void setup() {
        ThemeResource notCachedFileIcon = new ThemeResource(
                "../runo/icons/16/document.png?" + new Date().getTime());
        ThemeResource notCachedFolderIconHuge = new ThemeResource(
                "../runo/icons/64/folder.png?" + new Date().getTime());
        ThemeResource notCachedFolderIconLarge = new ThemeResource(
                "../runo/icons/32/folder.png?" + new Date().getTime());
        ThemeResource notCachedFolderIconLargeOther = new ThemeResource(
                "../runo/icons/32/ok.png?" + new Date().getTime());

        Tree t = new Tree();
        t.setImmediate(true);
        t.addStyleName(Reindeer.TREE_CONNECTORS);

        for (int i = 1; i <= 5; i++) {
            String item = "Root " + i;
            t.addItem(item);
            if (i == 1) {
                t.setItemIcon(item, notCachedFileIcon);
                addChildren(t, item, true);
            } else if (i == 2) {
                t.setItemIcon(item, notCachedFolderIconHuge);
                addChildren(t, item, false);
            } else if (i == 3) {
                t.setItemIcon(item, notCachedFolderIconLarge);
                addChildren(t, item, true);
            } else if (i == 4) {
                t.setItemIcon(item, notCachedFolderIconLargeOther);
                addChildren(t, item, false);
            } else if (i == 5) {
                addChildren(t, item, true);
            }
        }

        Panel p = new Panel();
        p.addComponent(t);
        p.setSizeFull();
        getLayout().setSizeFull();

        addComponent(p);

        addDnD(t);
    }

    private void addDnD(final Tree t) {
        t.setDragMode(TreeDragMode.NODE);
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
                    hierarchicalContainer.addContainerProperty(object, idx
                            .getType(object), null);
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

                    ((HierarchicalContainer) t.getContainerDataSource())
                            .setParent(itemId, itemIdInto);

                    if (dropLocation == null) {
                        System.err.println("No detail of drop place available");
                    }
                    ((HierarchicalContainer) t.getContainerDataSource())
                            .moveAfterSibling(itemId, itemIdAfter);
                }

                return;
            }

            public AcceptCriterion getAcceptCriterion() {
                // TODO should actually check that source is same as target
                return AcceptItem.ALL;
            }

        };

        t.setDropHandler(itemSorter);
    }

    protected void addChildren(Tree t, String parent, boolean recurse) {
        for (int i = 1; i <= Math.max(3, 3 + Math.random() * 2); i++) {
            String item = parent + ", child " + i;
            t.addItem(item);
            t.setChildrenAllowed(parent, true);
            t.setParent(item, parent);
            if (recurse) {
                if (i % 2 == 0) {
                    addChildren(t, item, false);
                    t.expandItem(parent);
                } else {
                    t.setChildrenAllowed(item, false);
                }
            } else {
                t.setChildrenAllowed(item, false);
            }
        }
    }

    @Override
    protected String getDescription() {
        return "A tree using the 'connectors' stylename should have Windows-like dotted connector lines joining the different hierarchy levels.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6745;
    }

}