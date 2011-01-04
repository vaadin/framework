package com.vaadin.tests.components.tree;

import java.util.Iterator;

import com.vaadin.demo.sampler.ExampleUtil;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;

public class TreeHorizontalResize extends TestBase {

    @Override
    protected void setup() {
        Panel treePanel = new Panel();
        treePanel.setHeight("500px");
        treePanel.setWidth(null);
        treePanel.getContent().setSizeUndefined();
        addComponent(treePanel);

        Tree tree = new Tree();
        tree.setContainerDataSource(ExampleUtil.getHardwareContainer());
        tree.setItemCaptionPropertyId(ExampleUtil.hw_PROPERTY_NAME);
        for (Iterator<?> it = tree.rootItemIds().iterator(); it.hasNext();) {
            tree.expandItemsRecursively(it.next());
        }
        treePanel.addComponent(tree);
    }

    @Override
    protected String getDescription() {
        return "The Tree should be properly resized horizontally when collapsing/expanding nodes. The height is fixed to 500px.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6230;
    }

}
