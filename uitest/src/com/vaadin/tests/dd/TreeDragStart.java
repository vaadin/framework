package com.vaadin.tests.dd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.Not;
import com.vaadin.event.dd.acceptcriteria.Or;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.AbstractSelect.VerticalLocationIs;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeTargetDetails;

public class TreeDragStart extends TestBase {

    @Override
    protected void setup() {
        final Tree tree = new Tree("Inventory");

        CheckBox checkBox = new CheckBox("Enabled");
        checkBox.setImmediate(true);
        checkBox.setValue(true);
        checkBox.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                tree.setEnabled(!tree.isEnabled());
            }
        });
        addComponent(checkBox);
        checkBox = new CheckBox("Drag start");
        checkBox.setImmediate(true);
        checkBox.setValue(true);
        checkBox.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (((CheckBox) event.getProperty()).getValue()) {
                    tree.setDragMode(TreeDragMode.NODE);
                } else {
                    tree.setDragMode(TreeDragMode.NONE);
                }
            }
        });
        addComponent(checkBox);

        tree.setContainerDataSource(createTreeContent());
        tree.setItemCaptionMode(Tree.ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID);
        for (Object item : tree.getItemIds().toArray()) {
            tree.setItemCaption(item, (String) ((BeanItem<?>) item)
                    .getItemProperty("name").getValue());
        }
        getLayout().addComponent(tree);

        // Expand all items
        for (Iterator<?> it = tree.rootItemIds().iterator(); it.hasNext();) {
            tree.expandItemsRecursively(it.next());
        }

        // Set the tree in drag source mode
        tree.setDragMode(TreeDragMode.NODE);

        // Allow the tree to receive drag drops and handle them
        tree.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                // Accept drops in the middle of container items
                // and below and above all items.
                return new Or(Tree.TargetItemAllowsChildren.get(), new Not(
                        VerticalLocationIs.MIDDLE));
            }

            @Override
            public void drop(DragAndDropEvent event) {
                // Wrapper for the object that is dragged
                DataBoundTransferable t = (DataBoundTransferable) event
                        .getTransferable();

                TreeTargetDetails target = (TreeTargetDetails) event
                        .getTargetDetails();

                // Get ids of the dragged item and the target item
                Object sourceItemId = t.getData("itemId");
                Object targetItemId = target.getItemIdOver();

                // On which side of the target the item was dropped
                VerticalDropLocation location = target.getDropLocation();

                HierarchicalContainer container = (HierarchicalContainer) tree
                        .getContainerDataSource();

                BeanItem<?> beanItem = null;
                if (sourceItemId instanceof BeanItem<?>) {
                    beanItem = (BeanItem<?>) sourceItemId;
                } else if (sourceItemId instanceof InventoryObject) {
                    beanItem = new BeanItem<InventoryObject>(
                            (InventoryObject) sourceItemId);
                }

                // Remove the item from the source container and
                // add it to the tree's container
                Container sourceContainer = t.getSourceContainer();
                sourceContainer.removeItem(sourceItemId);
                tree.addItem(beanItem);
                InventoryObject bean = (InventoryObject) beanItem.getBean();
                tree.setChildrenAllowed(beanItem, bean.isContainer());

                // Drop right on an item -> make it a child
                if (location == VerticalDropLocation.MIDDLE) {
                    tree.setParent(beanItem, targetItemId);
                } else if (location == VerticalDropLocation.TOP) {
                    Object parentId = container.getParent(targetItemId);
                    tree.setParent(beanItem, parentId);
                    container.moveAfterSibling(beanItem, targetItemId);
                    container.moveAfterSibling(targetItemId, beanItem);
                }

                // Drop below another item -> make it next
                else if (location == VerticalDropLocation.BOTTOM) {
                    Object parentId = container.getParent(targetItemId);
                    tree.setParent(beanItem, parentId);
                    container.moveAfterSibling(beanItem, targetItemId);
                }

                tree.setItemCaption(beanItem, bean.getName());
            }
        });

        // Have a table that allows dragging from
        final Table table = new Table("Inventory List");
        table.setDragMode(TableDragMode.ROW);

        // Initialize the table container
        ArrayList<InventoryObject> collection = new ArrayList<InventoryObject>();
        collection.add(new InventoryObject("Dummy Item", 0.0, false));
        final BeanItemContainer<InventoryObject> tableContainer = new BeanItemContainer<InventoryObject>(
                collection);
        table.setContainerDataSource(tableContainer);
        table.setVisibleColumns(new String[] { "name", "weight" });
        table.removeAllItems();

        // Allow the table to receive drops and handle them
        table.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return new Not(VerticalLocationIs.MIDDLE);
            }

            @Override
            public void drop(DragAndDropEvent event) {
                // Wrapper for the object that is dragged
                DataBoundTransferable t = (DataBoundTransferable) event
                        .getTransferable();

                // Make sure the drag source is the same tree
                if (t.getSourceComponent() != tree
                        && t.getSourceComponent() != table) {
                    return;
                }

                AbstractSelectTargetDetails target = (AbstractSelectTargetDetails) event
                        .getTargetDetails();

                // Get ids of the dragged item and the target item
                Object sourceItemId = t.getData("itemId");
                Object targetItemId = target.getItemIdOver();

                // Do not allow drop on the item itself
                if (sourceItemId.equals(targetItemId)) {
                    return;
                }

                InventoryObject bean = null;
                if (sourceItemId instanceof BeanItem<?>) {
                    bean = (InventoryObject) ((BeanItem<?>) sourceItemId)
                            .getBean();
                } else if (sourceItemId instanceof InventoryObject) {
                    bean = (InventoryObject) sourceItemId;
                }

                // Remove the item from the source container
                t.getSourceContainer().removeItem(sourceItemId);

                // On which side of the target the item was dropped
                VerticalDropLocation location = target.getDropLocation();

                // The table was empty or otherwise not on an item
                if (targetItemId == null) {
                    tableContainer.addItem(bean); // Add to the end
                } else if (location == VerticalDropLocation.TOP) {
                    tableContainer.addItemAt(
                            tableContainer.indexOfId(targetItemId), bean);
                } else if (location == VerticalDropLocation.BOTTOM) {
                    tableContainer.addItemAfter(targetItemId, bean);
                }
            }
        });
        getLayout().addComponent(table);
    }

    public class InventoryObject implements Serializable {
        private static final long serialVersionUID = -8943498783302996516L;

        String name;
        double weight;
        boolean container;

        public InventoryObject(String name, double weight, boolean container) {
            this.name = name;
            this.weight = weight;
            this.container = container;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public boolean isContainer() {
            return container;
        }

        public void setContainer(boolean container) {
            this.container = container;
        }
    }

    HashMap<String, InventoryObject> inventoryStore = new HashMap<String, InventoryObject>();

    public HierarchicalContainer createTreeContent() {
        final Object[] inventory = new Object[] {
                new InventoryObject("root", 0.0, true),
                new InventoryObject("+5 Quarterstaff (blessed)", 3.5, false),
                new InventoryObject("+3 Elven Dagger (blessed)", 0.2, false),
                new InventoryObject("+5 Helmet (greased)", 1.5, false),
                new Object[] {
                        new InventoryObject("Sack", 0.2, true),
                        new InventoryObject("Pick-Axe", 2.5, false),
                        new InventoryObject("Lock Pick", 0.1, false),
                        new InventoryObject("Tinning Kit", 0.5, false),
                        new InventoryObject("Potion of Healing (blessed)", 0.7,
                                false), },
                new Object[] {
                        new InventoryObject("Bag of Holding", 0.1, true),
                        new InventoryObject("Magic Marker", 0.05, false),
                        new InventoryObject("Can of Grease (blessed)", 0.5,
                                false), },
                new Object[] {
                        new InventoryObject("Chest", 10.0, true),
                        new InventoryObject("Scroll of Identify", 0.1, false),
                        new InventoryObject("Scroll of Genocide", 0.1, false),
                        new InventoryObject("Towel", 0.3, false),
                        new Object[] {
                                new InventoryObject("Large Box", 8.0, true),
                                new InventoryObject("Figurine of Vaadin", 0.4,
                                        false),
                                new InventoryObject("Expensive Camera", 1.5,
                                        false), },
                        new InventoryObject("Tin Opener", 0.02, false), }, };

        HierarchicalContainer container = new HierarchicalContainer();

        new Object() {
            public void put(Object[] data, Object parent,
                    HierarchicalContainer container) {
                for (int i = 1; i < data.length; i++) {
                    BeanItem<InventoryObject> item;
                    if (data[i].getClass() == InventoryObject.class) {
                        InventoryObject object = (InventoryObject) data[i];
                        item = new BeanItem<InventoryObject>(object);
                        container.addItem(item);
                        container.setParent(item, parent);
                        container.setChildrenAllowed(item, false);
                    } else {// It's an Object[]
                        Object[] sub = (Object[]) data[i];
                        InventoryObject object = (InventoryObject) sub[0];
                        item = new BeanItem<InventoryObject>(object);
                        container.addItem(item);
                        container.setParent(item, parent);

                        // Add children recursively
                        put(sub, item, container);
                    }

                    inventoryStore
                            .put(item.getBean().getName(), item.getBean());
                }
            }
        }.put(inventory, null, container);

        return container;
    }

    @Override
    protected String getDescription() {
        return "Should work";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6320;
    }

}
