/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import java.sql.SQLException;

import com.vaadin.data.util.QueryContainer;
import com.vaadin.demo.util.SampleDatabase;
import com.vaadin.event.Action;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

/**
 * This example shows how Table, Select and Tree UI components can use
 * Containers. QueryContainer is used to bind SQL table rows into Vaadin UI
 * components. Table has few example actions added. Also embedding XHTML through
 * Label components is used. Demonstrates: how to create
 * <code>com.vaadin.data.Container</code> and set it as datasource to UI
 * components <code>com.vaadin.ui.Component.Tree</code>, how to receive
 * ExpandEvent and implement <code>com.vaadin.ui.Tree.ExpandListener</code>, how
 * to use <code>com.vaadin.event.Action</code>.
 * 
 * @author IT Mill Ltd.
 * @since 4.0.0
 * 
 */
public class QueryContainerDemo extends com.vaadin.Application implements
        Action.Handler {

    private static final String ACTION_DESCRIPTION = "Try right mouse button to initiate "
            + "actions menu.<br />Note: on Opera you use meta key "
            + "and left mouse button.";

    private static final String TABLE_CAPTION = SampleDatabase.ROWCOUNT
            + " dynamically loaded rows from example SQL table";

    // Table component where SQL rows are attached (using QueryContainer)
    private final Table table = new Table();

    private final Label tableLastAction = new Label(
            "No action selected for table.");

    // Select component where SQL rows are attached (using QueryContainer)
    private final Select select = new Select();

    // Tree component that uses select as datasource
    private final Tree tree = new Tree();

    private final Label treeLastAction = new Label(
            "No action selected for tree.");

    // Database provided with sample data
    private SampleDatabase sampleDatabase;

    // Example Actions for table
    private final Action ACTION1 = new Action("Upload");

    private final Action ACTION2 = new Action("Download");

    private final Action ACTION3 = new Action("Show history");

    private final Action[] actions = new Action[] { ACTION1, ACTION2, ACTION3 };

    /**
     * Initialize Application. Demo components are added to main window.
     */
    @Override
    public void init() {
        final Window main = new Window("QueryContainer demo");
        setMainWindow(main);

        // Main window contains heading, table, select and tree
        main
                .addComponent(new Label(
                        "<h2>QueryContainer demo</h2>"
                                + "<b>Rows are loaded from the server as they are needed.<br />"
                                + "Try scrolling the table to see it in action.</b><br />"
                                + ACTION_DESCRIPTION, Label.CONTENT_XHTML));
        main.addComponent(table);
        main.addComponent(tableLastAction);
        main.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
        main.addComponent(select);
        main.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
        main.addComponent(tree);
        main.addComponent(treeLastAction);

        // create demo database
        sampleDatabase = new SampleDatabase();

        // initialize demo components
        initTable();
        initSelect();
        initTree();
    }

    /**
     * Populates table component with all rows from employee table.
     * 
     */
    private void initTable() {
        // init table
        table.setCaption(TABLE_CAPTION);
        table.setPageLength(10);
        table.setSelectable(true);
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        // this class handles table actions (see handleActions method below)
        table.addActionHandler(this);
        table.setDescription(ACTION_DESCRIPTION);

        // populate Vaadin table component with test SQL table rows
        try {
            final QueryContainer qc = new QueryContainer(
                    "SELECT * FROM employee", sampleDatabase.getConnection());
            table.setContainerDataSource(qc);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        // define which columns should be visible on Table component
        table.setVisibleColumns(new Object[] { "FIRSTNAME", "LASTNAME",
                "TITLE", "UNIT" });
        table.setItemCaptionPropertyId("ID");
    }

    /**
     * Populates select component with distinct unit values from employee table.
     * 
     */
    private void initSelect() {
        // init select
        select.setCaption("All distinct units from employee table.");
        select.setItemCaptionPropertyId("UNIT");

        // populate Vaadin select component with test SQL table rows
        try {
            final QueryContainer qc = new QueryContainer(
                    "SELECT DISTINCT UNIT FROM employee", sampleDatabase
                            .getConnection());
            select.setContainerDataSource(qc);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates tree component using select component as data source for root
     * nodes, child nodes are queried from database. Implementation is done for
     * example purposes only.
     * 
     */
    private void initTree() {
        // init tree
        tree.setCaption("All distinct units from employee table.");
        tree.setItemCaptionPropertyId("UNIT");
        tree.setSelectable(true);
        // this class handles tree actions (see handleActions method below)
        tree.addActionHandler(this);
        tree.setDescription("Try right mouse button to initiate "
                + "actions menu. Note: on Opera you use meta key "
                + "and left mouse button.");

        // Populate Vaadin Tree using select component as data source
        tree.setContainerDataSource(select.getContainerDataSource());
    }

    /**
     * Return example actions
     */
    public Action[] getActions(Object target, Object sender) {
        return actions;
    }

    /**
     * Executed by right mouse button on table or tree component.
     */
    public void handleAction(Action action, Object sender, Object target) {
        if (sender == table) {
            tableLastAction.setValue("Last action clicked was '"
                    + action.getCaption() + "' on item " + target);
        } else if (sender == tree) {
            treeLastAction.setValue("Last action clicked was '"
                    + action.getCaption() + "' on item " + target);
        }
    }

}
