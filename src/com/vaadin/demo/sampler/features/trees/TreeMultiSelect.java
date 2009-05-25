package com.vaadin.demo.sampler.features.trees;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Tree;

@SuppressWarnings("serial")
public class TreeMultiSelect extends Feature {
    @Override
    public String getName() {
        return "Tree, multiple selections";
    }

    @Override
    public String getDescription() {
        return "In this example, you can select multiple tree nodes"
                + " and delete the selected items. Click a selected item again to de-select it.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Tree.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { TreeSingleSelect.class, TreeActions.class,
                TreeMouseEvents.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }
}
