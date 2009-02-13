package com.itmill.toolkit.demo.sampler.features.trees;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Tree;

public class TreeMouseEvents extends Feature {
    @Override
    public String getName() {
        return "Tree, mouse events";
    }

    @Override
    public String getDescription() {
        return "In this example, selecting items from the tree"
                + " is disabled. Instead, another method of selection"
                + " is used. Using the ItemClickEvent, we can update the"
                + " label showing the selection."
                + "<br>Try to click your left, right and middle mouse"
                + " buttons on the tree items. Any modifier keys will"
                + " also be detected.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Tree.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { TreeSingleSelect.class, TreeMultiSelect.class,
                TreeActions.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }
}
