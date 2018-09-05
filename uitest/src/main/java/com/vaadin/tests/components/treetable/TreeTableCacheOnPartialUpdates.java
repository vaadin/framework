package com.vaadin.tests.components.treetable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.NativeButton;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Container.Ordered;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.ContainerHierarchicalWrapper;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;
import com.vaadin.v7.ui.TreeTable;

public class TreeTableCacheOnPartialUpdates extends TestBase {
    private Log log = new Log(5);

    public class TestBean implements Serializable {
        private String col1;
        private String col2;

        public TestBean() {
            col1 = "";
            col2 = "";
        }

        public TestBean(String col1, String col2) {
            this.col1 = col1;
            this.col2 = col2;
        }

        public String getCol1() {
            return col1;
        }

        public void setCol1(String col1) {
            this.col1 = col1;
        }

        public String getCol2() {
            return col2;
        }

        public void setCol2(String col2) {
            this.col2 = col2;
        }

        @Override
        public String toString() {
            return "HierarchicalTestBean [col1=" + col1 + ", col2=" + col2
                    + "]";
        }

    }

    public class Col3ColumnGenerator implements ColumnGenerator {
        @Override
        public Component generateCell(final com.vaadin.v7.ui.Table source,
                final Object itemId, Object columnId) {
            TestBean tb = (TestBean) itemId;
            String identifier = "Item " + itemId + "/" + columnId;
            Button btnCol3 = new NativeButton(identifier);
            btnCol3.setId(
                    "cacheTestButton-" + tb.getCol1() + "-" + tb.getCol2());
            btnCol3.addClickListener(event -> log.log("Button "
                    + event.getButton().getCaption() + " clicked. Row index: "
                    + indexOfId(source, itemId)));
            return btnCol3;
        }

    }

    public class Col4ColumnGenerator implements ColumnGenerator {
        @Override
        public Component generateCell(final com.vaadin.v7.ui.Table source,
                final Object itemId, Object columnId) {
            TestBean tb = (TestBean) itemId;
            String identifier = "Expand/Collapse";
            Button btnCol4 = new NativeButton(identifier);
            btnCol4.setId("cacheTestButtonToggle-" + tb.getCol1() + "-"
                    + tb.getCol2());
            btnCol4.addClickListener(event -> treeTable.setCollapsed(itemId,
                    !treeTable.isCollapsed(itemId)));
            return btnCol4;
        }
    }

    protected int indexOfId(Table source, Object itemId) {
        Container.Ordered c = (Ordered) source.getContainerDataSource();
        if (c instanceof Container.Indexed) {
            return ((Container.Indexed) source).indexOfId(itemId);
        } else {
            ArrayList<Object> list = new ArrayList<>(source.getItemIds());
            return list.indexOf(itemId);

        }
    }

    private TreeTable treeTable;
    private BeanItemContainer<TestBean> testBeanContainer;
    private static String[] columnHeaders = { "Col1", "Col2", "Col3", "Col4" };
    private static Object[] visibleColumns = { "col1", "col2", "col3", "col4" };

    @Override
    public void setup() {
        // Force row height to be the same in all browsers so scrolling based on
        // pixels works as expected
        Button b = new Button("Show first");
        addComponent(b);
        b.addClickListener(event -> log.log("First visible item id is: "
                + treeTable.getCurrentPageFirstItemId()));
        NativeSelect cacheRateSelect = new NativeSelect("Cache rate");
        cacheRateSelect.setImmediate(true);
        cacheRateSelect.setNullSelectionAllowed(false);
        cacheRateSelect.addItem(new Integer(0));
        cacheRateSelect.addItem(new Integer(1));
        cacheRateSelect.addItem(new Integer(2));
        cacheRateSelect.setValue(2);
        cacheRateSelect.addValueChangeListener(event -> treeTable
                .setCacheRate((Integer) event.getProperty().getValue()));
        addComponent(cacheRateSelect);
        treeTable = new TreeTable();
        treeTable.addStyleName("table-equal-rowheight");
        testBeanContainer = new BeanItemContainer<>(TestBean.class);

        Map<String, Integer> hasChildren = new HashMap<>();
        hasChildren.put("1", 5);
        hasChildren.put("3", 10);
        hasChildren.put("5", 20);
        hasChildren.put("6", 7);
        hasChildren.put("7", 1);
        hasChildren.put("40", 20);
        hasChildren.put("99", 20);
        treeTable.setContainerDataSource(createContainer(100, hasChildren));
        treeTable.addGeneratedColumn("col3", new Col3ColumnGenerator());
        treeTable.addGeneratedColumn("col4", new Col4ColumnGenerator());
        treeTable.addExpandListener(
                event -> logExpandCollapse(event.getItemId(), "expanded"));
        treeTable.addCollapseListener(
                event -> logExpandCollapse(event.getItemId(), "collapsed"));
        treeTable.setVisibleColumns(visibleColumns);
        treeTable.setColumnHeaders(columnHeaders);
        treeTable.setColumnWidth("col1", 150);
        treeTable.setColumnWidth("col2", 50);
        treeTable.setHeight("430px");
        addComponent(log);
        addComponent(treeTable);
    }

    protected void logExpandCollapse(Object itemId, String operation) {
        String identifier = "Item " + itemId;
        log.log("Row " + identifier + " " + operation + ". Row index: "
                + indexOfId(treeTable, itemId));
    }

    private Container createContainer(int items,
            Map<String, Integer> hasChildren) {
        ContainerHierarchicalWrapper container;

        container = new ContainerHierarchicalWrapper(testBeanContainer);
        populate(container, items, hasChildren);
        return container;
    }

    private void populate(Container.Hierarchical container, int items,
            Map<String, Integer> children) {

        for (int i = 0; i < items; i++) {
            String row = String.valueOf(i + 1);
            String rowText = stringForInt(i);

            TestBean itemId = new TestBean(row, rowText);
            container.addItem(itemId);
            if (children.containsKey(row)) {
                itemId.setCol1(itemId.getCol1() + " (children)");
                for (int j = 0; j < children.get(row); j++) {
                    TestBean childItemId = new TestBean(row + "." + (j + 1),
                            rowText + "." + stringForInt(j));
                    container.addItem(childItemId);
                    container.setParent(childItemId, itemId);
                }
            }

        }
    }

    private String stringForInt(int i) {
        int charsAllowed = 26; // a-z
        if (i >= charsAllowed) {
            return stringForInt(i / charsAllowed - 1)
                    + stringForInt(i % charsAllowed);
        } else {
            return String.valueOf((char) (i + 'A'));
        }
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }
}
