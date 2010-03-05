package com.vaadin.tests.dd;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.demo.tutorial.addressbook.data.Person;
import com.vaadin.demo.tutorial.addressbook.data.PersonContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptCriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptCriteria.And;
import com.vaadin.event.dd.acceptCriteria.DragSourceIs;
import com.vaadin.event.dd.acceptCriteria.IsDataBound;
import com.vaadin.event.dd.acceptCriteria.Or;
import com.vaadin.event.dd.acceptCriteria.ServerSideCriterion;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.AbstractSelectDropTargetDetails;
import com.vaadin.ui.Tree.OverFolderNode;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeDropTargetDetails;

public class DDTest2 extends TestBase {

    java.util.Random r = new java.util.Random(1);

    HorizontalLayout hl = new HorizontalLayout();
    Tree tree1 = new Tree("Tree that accepts table rows to folders");
    Table table = new Table("Drag rows to Tree on left or right");
    Tree tree2 = new Tree("Accepts items, copies values");

    private Tree tree3;

    @Override
    protected void setup() {
        Window w = getLayout().getWindow();
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
        final AcceptCriterion crit = new ServerSideCriterion() {
            public boolean accepts(DragAndDropEvent dragEvent) {

                TreeDropTargetDetails dropTargetData = (TreeDropTargetDetails) dragEvent
                        .getDropTargetDetails();

                Object itemIdOver = dropTargetData.getItemIdOver();

                int i = r.nextInt();
                if (i % 2 == 0) {
                    return true;
                }
                return false;
            }
        };

        tree3.setDropHandler(new DropHandler() {
            public void drop(DragAndDropEvent dropEvent) {
                Transferable transferable = dropEvent.getTransferable();
                String data = (String) transferable.getData("Text");
                if (data == null) {
                    data = "-no Text data flawor-";
                }
                tree3.addItem(data);
                AbstractSelect.AbstractSelectDropTargetDetails dropTargetData = (AbstractSelect.AbstractSelectDropTargetDetails) dropEvent
                        .getDropTargetDetails();
                tree3.setParent(data, dropTargetData.getItemIdOver());

            }

            public AcceptCriterion getAcceptCriterion() {
                return crit;
            }
        });

        addComponent(tree3);

        /*
         * Make table rows draggable
         */
        table.setDragMode(Table.DragModes.ROWS);

        OverFolderNode onNode = new OverFolderNode();
        DragSourceIs fromTable = new DragSourceIs(table);

        DragSourceIs fromTree = new DragSourceIs(tree1);
        final Or fromTree1OrTable = new Or(fromTable, fromTree);
        // Or could in the case be replaced with, keeping here as an example and
        // test
        DragSourceIs treeOrTable = new DragSourceIs(table, tree1);

        final And and = new And(fromTree1OrTable, onNode);

        DropHandler dropHandler = new DropHandler() {

            public void drop(DragAndDropEvent event) {
                /*
                 * We know transferrable is from table, so it is of type
                 * DataBindedTransferrable
                 */
                DataBoundTransferable tr = (DataBoundTransferable) event
                        .getTransferable();
                Object itemId = tr.getItemId();
                Container sourceContainer = (Container) tr.getSourceComponent();
                if (sourceContainer != tree1) {
                    // if the source is from table (not from tree1 itself),
                    // transfer Name property and use it as an indentifier in
                    // tree1
                    String name = sourceContainer.getItem(itemId)
                            .getItemProperty("Name").toString();

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
                AbstractSelectDropTargetDetails details = (AbstractSelectDropTargetDetails) event
                        .getDropTargetDetails();
                Object idOver = details.getItemIdOver();
                tree1.setParent(itemId, idOver);

            }

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
            public void drop(DragAndDropEvent event) {
                AbstractSelectDropTargetDetails details = (AbstractSelectDropTargetDetails) event
                        .getDropTargetDetails();
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
                        tree2.setItemCaption(itemId, tree2.getItemCaption(tr
                                .getItemId()));
                    }
                }
            }

            private void copySubTree(Object itemId, Object itemIdTo) {
                Collection children = tree1.getChildren(itemId);
                if (children != null) {
                    for (Object childId : children) {
                        Object newItemId = tree2.addItem();
                        tree2.setItemCaption(newItemId, (String) childId);
                        tree2.setParent(newItemId, itemIdTo);
                        copySubTree(childId, newItemId);
                    }
                }
            }

            public AcceptCriterion getAcceptCriterion() {
                return IsDataBound.get();
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
