package com.itmill.toolkit.demo.sampler.features.table;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Component;

public class TableMouseEvents extends Feature {

    @Override
    public Component getExample() {
        return new TableStylingExample();
    }

    @Override
    public String getDescription() {
        return "An ItemClickListener can be used to react to mouse click events. Different buttons, double click, and modifier keys can be detected.<br/>Double-click a first or last name to toggle it's marked state.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class[] getRelatedFeatures() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
