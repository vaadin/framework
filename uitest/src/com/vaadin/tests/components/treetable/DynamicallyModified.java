package com.vaadin.tests.components.treetable;

import java.util.Collection;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.TreeTable;

public class DynamicallyModified extends TestBase implements
        Tree.ExpandListener, Tree.CollapseListener {
    protected static final String NAME_PROPERTY = "Name";
    protected static final String HOURS_PROPERTY = "Hours done";
    protected static final String MODIFIED_PROPERTY = "Last Modified";
    private static final String DEFAULT_DATE = "Wed Nov 30 14:40:26 EET 2011";

    protected TreeTable treetable;

    @Override
    protected void setup() {
        getLayout().setWidth("100%");

        // Create the treetable
        treetable = new TreeTable();
        treetable.setWidth("100%");
        treetable.addListener((Tree.ExpandListener) this);
        treetable.addListener((Tree.CollapseListener) this);

        addComponent(treetable);

        // Add Table columns
        treetable.addContainerProperty(NAME_PROPERTY, String.class, "");
        treetable.addContainerProperty(HOURS_PROPERTY, Integer.class, 0);
        treetable.addContainerProperty(MODIFIED_PROPERTY, String.class,
                DEFAULT_DATE);

        // Populate table
        Object allProjects = treetable.addItem(new Object[] { "All Projects",
                18, DEFAULT_DATE }, null);
        Object year2010 = treetable.addItem(new Object[] { "Year 2010", 18,
                DEFAULT_DATE }, null);
        Object customerProject1 = treetable.addItem(new Object[] {
                "Customer Project 1", 13, DEFAULT_DATE }, null);
        Object customerProject1Implementation = treetable.addItem(new Object[] {
                "Implementation", 5, DEFAULT_DATE }, null);
        Object customerProject1Planning = treetable.addItem(new Object[] {
                "Planning", 2, DEFAULT_DATE }, null);
        Object customerProject1Prototype = treetable.addItem(new Object[] {
                "Prototype", 5, DEFAULT_DATE }, null);
        Object customerProject2 = treetable.addItem(new Object[] {
                "Customer Project 2", 5, DEFAULT_DATE }, null);
        Object customerProject2Planning = treetable.addItem(new Object[] {
                "Planning", 5, DEFAULT_DATE }, null);

        // Set hierarchy
        treetable.setParent(year2010, allProjects);
        treetable.setParent(customerProject1, year2010);
        treetable.setParent(customerProject1Implementation, customerProject1);
        treetable.setParent(customerProject1Planning, customerProject1);
        treetable.setParent(customerProject1Prototype, customerProject1);
        treetable.setParent(customerProject2, year2010);
        treetable.setParent(customerProject2Planning, customerProject2);

        // Disallow children from leaves
        treetable.setChildrenAllowed(customerProject1Implementation, false);
        treetable.setChildrenAllowed(customerProject1Planning, false);
        treetable.setChildrenAllowed(customerProject1Prototype, false);
        treetable.setChildrenAllowed(customerProject2Planning, false);

        // Expand all
        treetable.setCollapsed(allProjects, false);
        treetable.setCollapsed(year2010, false);
        treetable.setCollapsed(customerProject1, false);
        treetable.setCollapsed(customerProject2, false);
    }

    @Override
    protected String getDescription() {
        return "Collaps 'Customer Project 1' will cause the first child if it to be removed. Expanding 'Custom Project 2' will cause a new child to be added. These events should be rendered correctly.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7780;
    }

    private int newChild = 1;

    @Override
    public void nodeExpand(ExpandEvent event) {
        Object expandedItemId = event.getItemId();
        // 7 == "Customer Project 1"
        if (expandedItemId != Integer.valueOf(7)) {
            return;
        }
        Object newChildId = treetable.addItem(new Object[] {
                "New child " + newChild++, 5, "Fri Nov 04 10:53:16 EET 2011" },
                null);
        treetable.setParent(newChildId, expandedItemId);
        treetable.setChildrenAllowed(newChildId, false);
    }

    @Override
    public void nodeCollapse(CollapseEvent event) {

        Object collapsedItemId = event.getItemId();

        // 3 == "Customer Project 1"
        if (collapsedItemId != Integer.valueOf(3)) {
            return;
        }
        @SuppressWarnings("unchecked")
        Collection<Object> childs = (Collection<Object>) treetable
                .getChildren(event.getItemId());

        if (childs == null) {
            return;
        }
        Object[] arr = childs.toArray();

        if (arr.length > 0) {
            treetable.removeItem(arr[0]);
        }

    }
}
