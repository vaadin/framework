package com.itmill.toolkit.demo.sampler.features.table;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Component;

public class TableColumnHeaders extends Feature {

    @Override
    public Component getExample() {
        return new TableMainFeaturesExample();
    }

    @Override
    public String getDescription() {
        return "A Table can optionally have column headers. The headers can contain a caption and an icon, and it's width can be changed by the user. A column can also (optionally) be moved by the user by dragging the column header, and (optionally) be sorted by clicking the header.";
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
