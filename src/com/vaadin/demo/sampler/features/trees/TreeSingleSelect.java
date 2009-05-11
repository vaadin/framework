package com.vaadin.demo.sampler.features.trees;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Tree;

public class TreeSingleSelect extends Feature {
    @Override
    public String getName() {
        return "Tree, single selection";
    }

    @Override
    public String getDescription() {
        return "In this example, you can select any single tree node"
                + " and modify its 'name' property. Click again to de-select.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Tree.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { TreeMultiSelect.class, TreeActions.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }
}
