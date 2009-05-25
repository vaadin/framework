package com.vaadin.demo.sampler.features.trees;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;

@SuppressWarnings("serial")
public class TreeActions extends Feature {
    @Override
    public String getName() {
        return "Tree, context menu";
    }

    @Override
    public String getDescription() {
        return "In this example, actions have been attached to"
                + " the tree component. Try clicking the secondary mouse"
                + " button on an item in the tree.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Tree.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { TreeSingleSelect.class, TreeMultiSelect.class,
                TreeMouseEvents.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Component getExample() {
        return new TreeSingleSelectExample();
    }
}
