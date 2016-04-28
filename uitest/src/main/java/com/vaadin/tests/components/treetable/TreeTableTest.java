package com.vaadin.tests.components.treetable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.tests.components.table.Tables;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.CollapseListener;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.TreeTable;

public class TreeTableTest extends Tables<TreeTable> implements
        CollapseListener, ExpandListener {

    @Override
    protected Class<TreeTable> getTestClass() {
        return TreeTable.class;
    }

    private int rootItemIds = 3;
    private CellStyleGenerator rootGreenSecondLevelRed = new com.vaadin.ui.Table.CellStyleGenerator() {

        @Override
        public String getStyle(Table source, Object itemId, Object propertyId) {
            if (propertyId != null) {
                return null;
            }

            Hierarchical c = getComponent().getContainerDataSource();
            if (c.isRoot(itemId)) {
                return "green";
            }

            Object parent = c.getParent(itemId);
            if (!c.isRoot(parent)) {
                return "red";
            }

            return null;
        }

        @Override
        public String toString() {
            return "Root green, second level red";
        }

    };

    private CellStyleGenerator evenItemsBold = new CellStyleGenerator() {

        @Override
        public String getStyle(Table source, Object itemId, Object propertyId) {
            if (propertyId != null) {
                return null;
            }

            Hierarchical c = getComponent().getContainerDataSource();
            int idx = 0;

            for (Iterator<?> i = c.getItemIds().iterator(); i.hasNext();) {
                Object id = i.next();
                if (id == itemId) {
                    if (idx % 2 == 1) {
                        return "bold";
                    } else {
                        return null;
                    }
                }

                idx++;
            }

            return null;
        }

        @Override
        public String toString() {
            return "Even items bold";
        }

    };

    @Override
    protected void createActions() {
        super.createActions();

        // Causes container changes so doing this first..
        createRootItemSelectAction(CATEGORY_DATA_SOURCE);

        createExpandCollapseActions(CATEGORY_FEATURES);
        createChildrenAllowedAction(CATEGORY_DATA_SOURCE);

        createListeners(CATEGORY_LISTENERS);
        // createItemStyleGenerator(CATEGORY_FEATURES);

        createBooleanAction("Animate collapse/expand", CATEGORY_STATE, false,
                animationCommand);

        // TODO: DropHandler
        // TODO: DragMode
        // TODO: ActionHandler

    }

    @Override
    protected Container createContainer(int properties, int items) {
        return createHierarchicalContainer(properties, items, rootItemIds);
    }

    private void createListeners(String category) {
        createBooleanAction("Item click listener", category, false,
                itemClickListenerCommand);
        createBooleanAction("Expand listener", category, false,
                expandListenerCommand);
        createBooleanAction("Collapse listener", category, false,
                collapseListenerCommand);

    }

    private Container.Hierarchical createHierarchicalContainer(int properties,
            int items, int roots) {
        Container.Hierarchical c = new HierarchicalContainer();

        populateContainer(c, properties, items);

        if (items <= roots) {
            return c;
        }

        // "roots" roots, each with
        // "firstLevel" children, two with no children (one with childAllowed,
        // one without)
        // ("firstLevel"-2)*"secondLevel" children ("secondLevel"/2 with
        // childAllowed, "secondLevel"/2 without)

        // N*M+N*(M-2)*C = items
        // items=N(M+MC-2C)

        // Using secondLevel=firstLevel/2 =>
        // items = roots*(firstLevel+firstLevel*firstLevel/2-2*firstLevel/2)
        // =roots*(firstLevel+firstLevel^2/2-firstLevel)
        // = roots*firstLevel^2/2
        // => firstLevel = sqrt(items/roots*2)

        int firstLevel = (int) Math.ceil(Math.sqrt(items / roots * 2.0));
        int secondLevel = firstLevel / 2;

        while (roots * (1 + 2 + (firstLevel - 2) * secondLevel) < items) {
            // Increase something so we get enough items
            secondLevel++;
        }

        List<Object> itemIds = new ArrayList<Object>(c.getItemIds());

        int nextItemId = roots;
        for (int rootIndex = 0; rootIndex < roots; rootIndex++) {
            // roots use items 0..roots-1
            Object rootItemId = itemIds.get(rootIndex);

            // force roots to be roots even though they automatically should be
            c.setParent(rootItemId, null);

            for (int firstLevelIndex = 0; firstLevelIndex < firstLevel; firstLevelIndex++) {
                if (nextItemId >= items) {
                    break;
                }
                Object firstLevelItemId = itemIds.get(nextItemId++);
                c.setParent(firstLevelItemId, rootItemId);

                if (firstLevelIndex < 2) {
                    continue;
                }

                // firstLevelChildren 2.. have child nodes
                for (int secondLevelIndex = 0; secondLevelIndex < secondLevel; secondLevelIndex++) {
                    if (nextItemId >= items) {
                        break;
                    }

                    Object secondLevelItemId = itemIds.get(nextItemId++);
                    c.setParent(secondLevelItemId, firstLevelItemId);
                }
            }
        }

        return c;
    }

    private void createRootItemSelectAction(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        for (int i = 1; i <= 10; i++) {
            options.put(String.valueOf(i), i);
        }
        options.put("20", 20);
        options.put("50", 50);
        options.put("100", 100);

        createSelectAction("Number of root items", category, options, "3",
                rootItemIdsCommand);
    }

    private void createExpandCollapseActions(String category) {
        LinkedHashMap<String, Object> options = new LinkedHashMap<String, Object>();

        for (Object id : getComponent().getItemIds()) {
            options.put(id.toString(), id);
        }
        createMultiClickAction("Expand", category, options, expandItemCommand,
                null);
        // createMultiClickAction("Expand recursively", category, options,
        // expandItemRecursivelyCommand, null);
        createMultiClickAction("Collapse", category, options,
                collapseItemCommand, null);

    }

    private void createChildrenAllowedAction(String category) {
        LinkedHashMap<String, Object> options = new LinkedHashMap<String, Object>();

        for (Object id : getComponent().getItemIds()) {
            options.put(id.toString(), id);
        }
        createMultiToggleAction("Children allowed", category, options,
                setChildrenAllowedCommand, true);

    }

    /*
     * COMMANDS
     */
    private Command<TreeTable, Integer> rootItemIdsCommand = new Command<TreeTable, Integer>() {

        @Override
        public void execute(TreeTable c, Integer value, Object data) {
            rootItemIds = value;
            updateContainer();
        }
    };

    private Command<TreeTable, Object> expandItemCommand = new Command<TreeTable, Object>() {

        @Override
        public void execute(TreeTable c, Object itemId, Object data) {
            c.setCollapsed(itemId, false);
        }
    };

    private Command<TreeTable, Object> collapseItemCommand = new Command<TreeTable, Object>() {

        @Override
        public void execute(TreeTable c, Object itemId, Object data) {
            c.setCollapsed(itemId, true);
        }
    };

    private Command<TreeTable, Boolean> setChildrenAllowedCommand = new Command<TreeTable, Boolean>() {

        @Override
        public void execute(TreeTable c, Boolean areChildrenAllowed,
                Object itemId) {
            c.setChildrenAllowed(itemId, areChildrenAllowed);
        }
    };

    private Command<TreeTable, Boolean> expandListenerCommand = new Command<TreeTable, Boolean>() {
        @Override
        public void execute(TreeTable c, Boolean value, Object data) {
            if (value) {
                c.addListener((ExpandListener) TreeTableTest.this);
            } else {
                c.removeListener((ExpandListener) TreeTableTest.this);
            }
        }
    };

    private Command<TreeTable, Boolean> collapseListenerCommand = new Command<TreeTable, Boolean>() {
        @Override
        public void execute(TreeTable c, Boolean value, Object data) {
            if (value) {
                c.addListener((CollapseListener) TreeTableTest.this);
            } else {
                c.removeListener((CollapseListener) TreeTableTest.this);
            }
        }
    };

    protected Command<TreeTable, Boolean> animationCommand = new Command<TreeTable, Boolean>() {

        @Override
        public void execute(TreeTable c, Boolean enabled, Object data) {
            c.setAnimationsEnabled(enabled);
        }
    };

    @Override
    public void nodeCollapse(CollapseEvent event) {
        log(event.getClass().getSimpleName() + ": " + event.getItemId());
    }

    @Override
    public void nodeExpand(ExpandEvent event) {
        log(event.getClass().getSimpleName() + ": " + event.getItemId());
    }
}
