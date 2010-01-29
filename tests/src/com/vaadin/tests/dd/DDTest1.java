package com.vaadin.tests.dd;

import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.AbstractDropHandler;
import com.vaadin.event.ComponentTransferrable;
import com.vaadin.event.DataBindedTransferrable;
import com.vaadin.event.Transferable;
import com.vaadin.event.AbstractDropHandler.AcceptCriterion;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragDropPane;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Table.DragModes;

/**
 * DD playground. Better quality example/prototype codes in {@link DDTest2}.
 */
public class DDTest1 extends TestBase {

    @Override
    protected void setup() {
        GridLayout gl = new GridLayout(3, 2);
        gl.setSizeFull();
        gl.setSpacing(true);
        Layout main = gl;

        DragDropPane pane1 = new DragDropPane();
        pane1.setSizeFull();
        pane1.setCaption("Pane1");

        Label label = new Label("Foo");
        label.setSizeUndefined();

        pane1.addComponent(label);

        Link l = new Link("This is link", new ExternalResource(
                "http://www.google.com/"));
        pane1.addComponent(l, "top:100px; left: 20px;");

        label = new Label("Bar");
        label.setSizeUndefined();
        pane1.addComponent(label);

        DragDropPane pane2 = new DragDropPane();
        pane2.setDebugId("pane2");
        pane2.setSizeFull();
        pane2
                .setCaption("Pane2 (accept needs server side visit, only \"Bar\")");

        AcceptCriterion f = new AcceptCriterion() {
            public boolean accepts(Transferable transferable) {
                // System.out.println("Simulating 500ms processing...");
                // try {
                // Thread.sleep(200);
                // } catch (InterruptedException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
                // System.out.println("Done get to work.");
                if (transferable instanceof ComponentTransferrable) {
                    ComponentTransferrable ct = (ComponentTransferrable) transferable;

                    Component component = ct.getSourceComponent();
                    if (component != null) {
                        if (component.toString() != null
                                && component.toString().contains("Bar")) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        pane2.getDropHandler().setAcceptCriterion(f);

        DragDropPane pane3 = new DragDropPane();
        pane3.setSizeFull();
        pane3.setCaption("Pane3");

        final Tree t = new Tree(
                "Tree with sorting enabled. Also allows dragging elsewhere.");

        final HierarchicalContainer idx = new HierarchicalContainer();
        t.setContainerDataSource(idx);
        t.setDebugId("perseys");
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

        /*
         * Moves items in tree (and could work in Table too). Also supports
         * "building" tree.
         * 
         * TODO fix algorithm, broken in some cases.
         */
        AbstractDropHandler itemSorter = new AbstractDropHandler() {

            @Override
            public void receive(Transferable transferable) {
                // TODO set properties, so same sorter could be used in Table
                if (transferable instanceof DataBindedTransferrable) {
                    DataBindedTransferrable transferrable2 = (DataBindedTransferrable) transferable;

                    Object itemId = transferrable2.getItemId();

                    Object itemIdOver = transferable.getData("itemIdOver");

                    String detail = ((String) transferable.getData("detail"))
                            .toLowerCase();

                    if ("center".equals(detail)) {
                        t.setParent(itemId, itemIdOver);
                        return;
                    } else if ("top".equals(detail)) {
                        // if on top of the caption area, add before
                        itemIdOver = idx.prevItemId(itemIdOver);
                    }

                    if (itemId.equals(itemIdOver)) {
                        // the location is same
                        return;
                    }

                    HierarchicalContainer subtree = getSubTree(idx, itemId);
                    boolean removed = idx.removeItem(itemId);

                    if (removed) {

                        if (detail == null) {
                            System.err
                                    .println("No detail of drop place available");
                        }

                        Item addItemAfter = idx
                                .addItemAfter(itemIdOver, itemId);
                        populateSubTree(idx, subtree, itemId);
                        // ensure the same parent as with related item
                        Object parent = idx.getParent(itemIdOver);
                        idx.setParent(itemId, parent);
                    }
                }

            }

            private void populateSubTree(HierarchicalContainer idx,
                    HierarchicalContainer subtree, Object itemId) {
                Collection children = subtree.getChildren(itemId);
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

            private HierarchicalContainer getSubTree(HierarchicalContainer idx,
                    Object itemId) {
                HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
                Collection containerPropertyIds = idx.getContainerPropertyIds();
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
                Collection children = source.getChildren(itemId);
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

        };

        /*
         * Accept only drags that have item identifiers
         */
        itemSorter
                .setAcceptCriterion(AbstractDropHandler.CRITERION_HAS_ITEM_ID);

        t.setDropHandler(itemSorter);

        Table ta = new Table("Test table");
        ta.setContainerDataSource(idx);
        ta.addContainerProperty("Foos", String.class, "Foo");
        ta.addContainerProperty("Bars", String.class, "Bar");
        ta.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
        ta.setSizeFull();
        ta.setDragMode(DragModes.ROWS);

        main.addComponent(pane1);
        main.addComponent(pane2);
        main.addComponent(pane3);
        main.addComponent(t);
        main.addComponent(ta);
        main.addComponent(new Link("Foo", new ExternalResource(
                "http://www.itmill.com/")));

        getLayout().setSizeFull();
        addComponent(main);

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
