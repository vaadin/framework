package com.vaadin.tests.contextclick;

import java.util.Collections;

import com.vaadin.data.TreeData;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeContextClickEvent;

public class TreeV8ContextClick extends
        AbstractContextClickUI<Tree<String>, TreeContextClickEvent<String>> {

    @Override
    protected Tree<String> createTestComponent() {
        TreeData<String> treeData = new TreeData<>();
        for (int i = 0; i < 3; i++) {
            String grandDad = "Granddad " + i;
            treeData.addItems(null, grandDad);
            for (int j = 0; j < 4; j++) {
                String dad = "Dad " + i + "/" + j;
                treeData.addItems(grandDad, dad);
                for (int k = 0; k < 5; k++) {
                    treeData.addItems(dad, "Son " + i + "/" + j + "/" + k);
                }
            }
        }
        Tree<String> tree = new Tree<>("Clane", treeData);
        tree.setWidth("100%");
        return tree;
    }

    @Override
    protected void handleContextClickEvent(
            TreeContextClickEvent<String> event) {
        String value = event.getItem();
        log("ContextClickEvent value: " + value);
    }

    @Override
    protected HorizontalLayout createContextClickControls() {
        HorizontalLayout controls = super.createContextClickControls();
        controls.addComponent(
                new Button("Remove all content", event -> {
                    testComponent.setItems(Collections.emptyList());
                    testComponent.setHeight("200px");
                }));
        return controls;
    }

}
