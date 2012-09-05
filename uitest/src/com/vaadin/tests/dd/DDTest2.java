package com.vaadin.tests.dd;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.And;
import com.vaadin.event.dd.acceptcriteria.Or;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.AbstractSelect.AcceptItem;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableTransferable;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TargetItemAllowsChildren;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class DDTest2 extends TestBase {

    java.util.Random r = new java.util.Random(1);

    HorizontalLayout hl = new HorizontalLayout();
    Tree tree1 = new Tree("Tree that accepts table rows to folders");
    Table table = new Table("Drag rows to Tree on left or right");
    Tree tree2 = new Tree("Accepts items, copies values");

    private Tree tree3;

    @Override
    protected void setup() {
        UI w = getLayout().getUI();
        /* darn reindeer has no icons */

        /* Make all trees (their nodes actually) draggable */
        tree1.setDragMode(TreeDragMode.NODE);
        tree2.setDragMode(TreeDragMode.NODE);

        hl.addComponent(tree1);
        hl.addComponent(table);
        hl.addComponent(tree2);
        hl.setWidth("100%");
        hl.setSpacing(true);
        hl.setExpandRatio(table, 1);
        popuplateTrees();
        table.setWidth("100%");
        table.setPageLength(10);
        populateTable();
        addComponent(hl);

        tree3 = new Tree(
                "Tree with lazy loading criteria, of first server visit caches accept rules for all captions");
        tree3.setDragMode(TreeDragMode.NODE);

        tree3.addItem("Drag on me");
        tree3.addItem("Or me");
        /*
         * An example of lazy initializing drop criterion with component
         * specific api for easy rule writing.
         * 
         * Example is pretty stupid (accepts drop on all nodes, but by
         * explicitly defining them here), but demonstrates lazy initialization
         * option if rules are heavy.
         */
        final AcceptCriterion crit = new Tree.TreeDropCriterion() {

            @Override
            protected Set<Object> getAllowedItemIds(DragAndDropEvent dragEvent,
                    Tree tree) {
                return new HashSet<Object>(tree.getItemIds());
            }
        };

        tree3.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent dropEvent) {
                Transferable transferable = dropEvent.getTransferable();

                String data = (String) transferable.getData("Text");
                if (transferable instanceof TableTransferable) {
                    TableTransferable tr = (TableTransferable) transferable;
                    System.out.println("From table row" + tr.getPropertyId());
                    Object value = tr.getSourceContainer()
                            .getItem(tr.getItemId())
                            .getItemProperty(tr.getPropertyId()).getValue();
                    data = (null != value) ? value.toString() : null;
                }
                if (data == null) {
                    data = "-no Text data flavor-";
                }
                tree3.addItem(data);
                AbstractSelect.AbstractSelectTargetDetails dropTargetData = (AbstractSelect.AbstractSelectTargetDetails) dropEvent
                        .getTargetDetails();
                tree3.setParent(data, dropTargetData.getItemIdOver());

            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return crit;
            }
        });

        addComponent(tree3);

        /*
         * Make table rows draggable
         */
        table.setDragMode(Table.TableDragMode.ROW);

        TargetItemAllowsChildren onNode = TargetItemAllowsChildren.get();
        SourceIs fromTable = new SourceIs(table);

        SourceIs fromTree = new SourceIs(tree1);
        final Or fromTree1OrTable = new Or(fromTable, fromTree);
        // Or could in the case be replaced with, keeping here as an example and
        // test
        @SuppressWarnings("unused")
        SourceIs treeOrTable = new SourceIs(table, tree1);

        final And and = new And(fromTree1OrTable, onNode);

        DropHandler dropHandler = new DropHandler() {

            @Override
            public void drop(DragAndDropEvent event) {
                /*
                 * We know transferrable is from table, so it is of type
                 * DataBoundTransferrable
                 */
                DataBoundTransferable tr = (DataBoundTransferable) event
                        .getTransferable();
                Object itemId = tr.getItemId();
                Container sourceContainer = tr.getSourceContainer();
                if (tr.getSourceComponent() != tree1) {
                    // if the source is from table (not from tree1 itself),
                    // transfer Name property and use it as an identifier in
                    // tree1
                    Object nameValue = sourceContainer.getItem(itemId)
                            .getItemProperty("Name").getValue();
                    String name = (null != nameValue) ? nameValue.toString()
                            : null;

                    tree1.addItem(name);
                    tree1.setChildrenAllowed(name, false);

                    /*
                     * Remove the item from table
                     */
                    sourceContainer.removeItem(itemId);

                    itemId = name;

                }

                /*
                 * As we also accept only drops on folders, we know dropDetails
                 * is from Tree and it contains itemIdOver.
                 */
                AbstractSelectTargetDetails details = (AbstractSelectTargetDetails) event
                        .getTargetDetails();
                Object idOver = details.getItemIdOver();
                tree1.setParent(itemId, idOver);

            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return and;
            }
        };
        tree1.setDropHandler(dropHandler);

        /*
         * First step done. tree1 now accepts drags only from table and only
         * over tree nodes aka "folders"
         */

        /*
         * Now set the rightmost tree accept any item drag. On drop, copy from
         * source. Also make drags from tree1 possible.
         */

        dropHandler = new DropHandler() {
            @Override
            public void drop(DragAndDropEvent event) {
                AbstractSelectTargetDetails details = (AbstractSelectTargetDetails) event
                        .getTargetDetails();
                Transferable transferable = event.getTransferable();

                if (transferable instanceof DataBoundTransferable) {
                    DataBoundTransferable tr = (DataBoundTransferable) transferable;

                    Object itemId = tree2.addItem();
                    tree2.setParent(itemId, details.getItemIdOver());
                    if (tr.getSourceComponent() == tree1) {
                        // use item id from tree1 as caption
                        tree2.setItemCaption(itemId, (String) tr.getItemId());
                        // if comes from tree1, move subtree too
                        copySubTree(tr.getItemId(), itemId);
                    } else if (tr.getSourceComponent() == table) {
                        // comes from table, override caption with name
                        String name = (String) table.getItem(tr.getItemId())
                                .getItemProperty("Name").getValue();
                        tree2.setItemCaption(itemId, name);
                    } else if (tr.getSourceComponent() == tree2) {
                        tree2.setItemCaption(itemId,
                                tree2.getItemCaption(tr.getItemId()));
                    }
                }
            }

            private void copySubTree(Object itemId, Object itemIdTo) {
                Collection<?> children = tree1.getChildren(itemId);
                if (children != null) {
                    for (Object childId : children) {
                        Object newItemId = tree2.addItem();
                        tree2.setItemCaption(newItemId, (String) childId);
                        tree2.setParent(newItemId, itemIdTo);
                        copySubTree(childId, newItemId);
                    }
                }
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptItem.ALL;
            }
        };

        tree2.setDropHandler(dropHandler);

        /*
         * Finally add two windows with DragDropPane. First accept anything,
         * second has server side accept rule to allow only drops from Tree1.
         * Check the code in implementing classes.
         */
        Window acceptAnyThing = new AcceptAnythingWindow();
        Window acceptFromTree1viaServerCheck = new AcceptFromComponent(tree1);

        w.addWindow(acceptAnyThing);
        acceptAnyThing.setPositionY(450);
        acceptAnyThing.setPositionX(150);
        w.addWindow(acceptFromTree1viaServerCheck);
        acceptFromTree1viaServerCheck.setPositionY(450);
        acceptFromTree1viaServerCheck.setPositionX(450);

    }

    private void populateTable() {
        table.addContainerProperty("Name", String.class, "");
        table.addContainerProperty("Weight", Integer.class, 0);

        PersonContainer testData = PersonContainer.createWithTestData();

        for (int i = 0; i < 10; i++) {
            Item addItem = table.addItem("Item" + i);
            Person p = testData.getIdByIndex(i);
            addItem.getItemProperty("Name").setValue(
                    p.getFirstName() + " " + p.getLastName());
            addItem.getItemProperty("Weight").setValue(50 + r.nextInt(60));
        }

    }

    private final static ThemeResource FOLDER = new ThemeResource(
            "../runo/icons/16/folder.png");
    private final static ThemeResource DOC = new ThemeResource(
            "../runo/icons/16/document.png");

    private void popuplateTrees() {
        HierarchicalContainer hc = new HierarchicalContainer();
        hc.addContainerProperty("icon", Resource.class, DOC);
        Item addItem = hc.addItem("Fats");
        addItem.getItemProperty("icon").setValue(FOLDER);
        hc.addItem("Tarja");
        hc.setParent("Tarja", "Fats");
        hc.setChildrenAllowed("Tarja", false);
        addItem = hc.addItem("Thins");
        addItem.getItemProperty("icon").setValue(FOLDER);
        addItem = hc.addItem("Anorectic");
        addItem.getItemProperty("icon").setValue(FOLDER);
        hc.setParent("Anorectic", "Thins");
        addItem = hc.addItem("Normal weighted");
        addItem.getItemProperty("icon").setValue(FOLDER);

        tree1.setContainerDataSource(hc);
        tree1.setItemIconPropertyId("icon");

        tree2.setContainerDataSource(new HierarchicalContainer());

        tree2.addItem("/");

    }

    @Override
    protected String getDescription() {
        return "dd";
    }

    @Override
    protected Integer getTicketNumber() {
        return 119;
    }

}
