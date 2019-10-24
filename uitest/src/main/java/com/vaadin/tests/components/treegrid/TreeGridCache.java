package com.vaadin.tests.components.treegrid;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.VerticalLayout;

public class TreeGridCache extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button button1 = new Button("Add TreeGrid1",
                e -> addComponent(addTreeGrid1()));
        button1.setId("button1");
        addComponent(button1);
        Button button2 = new Button("Add TreeGrid2",
                e -> addComponent(addTreeGrid2()));
        button2.setId("button2");
        addComponent(button2);
    }

    private TreeGrid<Integer> addTreeGrid1() {
        TreeData<Integer> treeData = new TreeData<>();
        treeData.addItems(null, 10, 5);
        treeData.addItems(10, 9, 8, 7, 6);
        treeData.addItems(5, 4, 3, 2, 1);
        treeData.addItems(1, 0, -1, -2, -3, -4);

        TreeDataProvider<Integer> treeDataProvider = new TreeDataProvider<>(
                treeData);

        TreeGrid<Integer> tree = new TreeGrid<>();
        tree.setCaption("TreeGrid1");
        tree.setHeightMode(HeightMode.ROW);
        tree.setHeightByRows(11);
        tree.setDataProvider(treeDataProvider);
        tree.addComponentColumn(integer -> {
            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.addComponent(new Label(integer.toString()));
            return verticalLayout;
        });
        tree.addColumn(i -> String.valueOf(i));
        treeDataProvider.refreshAll();
        return tree;
    }

    private TreeGrid<TestBean> addTreeGrid2() {
        TestBean parent = null;
        TreeGrid<TestBean> tree = new TreeGrid<>(TestBean.class);
        tree.setCaption("TreeGrid2");
        tree.setColumns("key", "value");
        tree.setHierarchyColumn("key");
        TreeData<TestBean> treeData = new TreeData<>();

        for (int i = 0; i < 2; i++) {
            TestBean val = new TestBean("root" + i, " root value " + i);
            treeData.addItem(parent, val);
            for (int j = 0; j < 10; j++) {
                TestBean v = new TestBean("leaf" + j, "leaf value" + j);
                treeData.addItem(val, v);
            }
        }
        TreeDataProvider<TestBean> dataProvider = new TreeDataProvider<>(
                treeData);
        tree.setDataProvider(dataProvider);
        tree.setHeight("500px");

        return tree;
    }

    @Override
    protected String getTestDescription() {
        return "Cache should cover more than 4 x the initial row amount after rows have been expanded. "
                + "Clicking on a row beyond that should not cause an exception.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11749;
    }

    public class TestBean {
        private String key;
        private String value;

        public TestBean(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
