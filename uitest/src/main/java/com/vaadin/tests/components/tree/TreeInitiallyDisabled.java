package com.vaadin.tests.components.tree;

import com.vaadin.data.TreeData;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Tree;

public class TreeInitiallyDisabled extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Tree<String> tree = new Tree<>();
        TreeData<String> treeData = new TreeData<>();
        String parent1 = "Parent 1";
        treeData.addItem(null, parent1);
        treeData.addItem(parent1, "Child 1.1");
        treeData.addItem(parent1, "Child 1.2");
        treeData.addItem(parent1, "Child 1.3");
        String parent2 = "Parent 2";
        treeData.addItem(null, parent2);
        treeData.addItem(parent2, "Child 2.1");
        treeData.addItem(parent2, "Child 2.2");
        treeData.addItem(parent2, "Child 2.3");
        String parent3 = "Parent 3";
        treeData.addItem(null, parent3);
        treeData.addItem(parent3, "Child 3.1");
        treeData.addItem(parent3, "Child 3.2");
        treeData.addItem(parent3, "Child 3.3");
        tree.setTreeData(treeData);

        Button button = new Button("Toggle enabled/disabled");
        button.addClickListener(event -> {
            tree.setEnabled(!tree.isEnabled());
        });

        addComponents(tree, button);

        tree.setEnabled(false);
    }

    @Override
    protected Integer getTicketNumber() {
        return 11831;
    }

    @Override
    protected String getTestDescription() {
        return "Initially disabled Tree should have disabled styles.";
    }
}
