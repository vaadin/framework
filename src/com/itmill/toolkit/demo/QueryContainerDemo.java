package com.itmill.toolkit.demo;

import java.sql.SQLException;

import com.itmill.toolkit.data.util.QueryContainer;
import com.itmill.toolkit.demo.util.SampleDatabase;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;

/**
 * This example shows how Table, Select and Tree UI components can use
 * Containers. QueryContainer is used to bind SQL table rows into Toolkit UI
 * components. Table has few example actions added. Also embedding XHTML through
 * Label components is used. Demonstrates: how to create
 * <code>com.itmill.toolkit.data.Container</code> and set it as datasource to
 * UI components <code>com.itmill.toolkit.ui.Component.Tree</code>, how to
 * receive ExpandEvent and implement
 * <code>com.itmill.toolkit.ui.Tree.ExpandListener</code>, how to use
 * <code>com.itmill.toolkit.event.Action</code>.
 * 
 * @author IT Mill Ltd.
 * @since 4.0.0
 * 
 */
public class QueryContainerDemo extends com.itmill.toolkit.Application
        implements Action.Handler {

    private static final String ACTION_DESCRIPTION = "Try right mouse button to initiate "
            + "actions menu.<br />Note: on Opera you use meta key "
            + "and left mouse button.";

    private static final String TABLE_CAPTION = SampleDatabase.ROWCOUNT
            + " dynamically loaded rows from example SQL table";

    // Table component where SQL rows are attached (using QueryContainer)
    private Table table = new Table();

    private Label tableLastAction = new Label("No action selected for table.");

    // Select component where SQL rows are attached (using QueryContainer)
    private Select select = new Select();

    // Tree component that uses select as datasource
    private Tree tree = new Tree();

    private Label treeLastAction = new Label("No action selected for tree.");

    // Database provided with sample data
    private SampleDatabase sampleDatabase;

    // Example Actions for table
    private Action ACTION1 = new Action("Upload");

    private Action ACTION2 = new Action("Download");

    private Action ACTION3 = new Action("Show history");

    private Action[] actions = new Action[] { ACTION1, ACTION2, ACTION3 };

    /**
     * Initialize Application. Demo components are added to main window.
     */
    public void init() {
        Window main = new Window("QueryContainer demo");
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

        // populate Toolkit table component with test SQL table rows
        try {
            QueryContainer qc = new QueryContainer("SELECT * FROM employee",
                    sampleDatabase.getConnection());
            table.setContainerDataSource(qc);
        } catch (SQLException e) {
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

        // populate Toolkit select component with test SQL table rows
        try {
            QueryContainer qc = new QueryContainer(
                    "SELECT DISTINCT UNIT FROM employee", sampleDatabase
                            .getConnection());
            select.setContainerDataSource(qc);
        } catch (SQLException e) {
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
        tree.setStyle("menu");

        // Populate Toolkit Tree using select component as data source
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
