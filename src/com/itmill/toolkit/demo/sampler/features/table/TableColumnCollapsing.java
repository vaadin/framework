package com.itmill.toolkit.demo.sampler.features.table;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Component;

public class TableColumnCollapsing extends Feature {

    @Override
    public Component getExample() {
        return new TableMainFeaturesExample();
    }

    @Override
    public String getDescription() {
        return "Columns can be 'collapsed', which means that it's not shown, but the user can make the column re-appear by using the menu in the upper right of the table.<br/>Columns can also be made invisible, in which case they can not be brought back by the user.";
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
