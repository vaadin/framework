package com.vaadin.tests.components.treegrid;

import java.util.stream.Stream;

import com.vaadin.data.TreeData;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.TreeGrid;

public class TreeGridChangingHierarchy extends AbstractTestUI {

    private static class TestDataProvider
            extends TreeDataProvider<String> {

        private TreeData<String> treeData;

        public TestDataProvider(TreeData<String> treeData) {
            super(treeData);
            this.treeData = treeData;
        }

        @Override
        public boolean hasChildren(String item) {
            if (!treeData.contains(item)) {
                return false;
            }
            return super.hasChildren(item);
        }

        @Override
        public Stream<String> fetchChildren(
                HierarchicalQuery<String, SerializablePredicate<String>> query) {
            if (!treeData.contains(query.getParent())) {
                return Stream.empty();
            }
            return super.fetchChildren(query);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        TreeData<String> data = new TreeData<>();
        data.addItems(null, "a", "b", "c").addItem("b", "b/a");

        TreeGrid<String> grid = new TreeGrid<>();
        grid.setDataProvider(new TestDataProvider(data));
        grid.addColumn(ValueProvider.identity());

        Button btn = new Button("add items to a and refresh");
        btn.addClickListener(event -> {
            data.addItems("a", "a/a", "a/b");
            grid.getDataProvider().refreshItem("a");
        });
        Button btn2 = new Button("add items to a/a and refresh");
        btn2.addClickListener(event -> {
            data.addItems("a/a", "a/a/a", "a/a/c").addItem("a/a/a", "a/a/a/a");
            grid.getDataProvider().refreshItem("a/a");
        });
        Button btn3 = new Button("remove a/a");
        btn3.addClickListener(event -> {
            data.removeItem("a/a");
        });
        Button btn4 = new Button("remove children of a/a");
        btn4.addClickListener(event -> {
            data.removeItem("a/a/a");
            data.removeItem("a/a/c");
        });
        Button btn5 = new Button("remove a");
        btn5.addClickListener(event -> {
            data.removeItem("a");
        });
        Button btn6 = new Button("remove children of a");
        btn6.addClickListener(event -> {
            data.removeItem("a/a");
            data.removeItem("a/b");
        });
        Button btn7 = new Button("remove children of a/a/a");
        btn7.addClickListener(event -> {
            data.removeItem("a/a/a/a");
        });

        addComponents(grid, btn, btn2, btn3, btn4, btn5, btn6, btn7);
    }
}
