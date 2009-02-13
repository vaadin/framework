package com.itmill.toolkit.demo.sampler.features.trees;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Tree;

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

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { TreeSingleSelect.class, TreeActions.class,
                TreeMouseEvents.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }
}
