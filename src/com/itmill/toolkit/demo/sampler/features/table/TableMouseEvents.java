package com.itmill.toolkit.demo.sampler.features.table;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Table;

public class TableMouseEvents extends Feature {

    @Override
    public Component getExample() {
        return new TableStylingExample();
    }

    public String getDescription() {
        return "Also known as a (Data)Grid, Table can be used to show data in"
                + " a tabular fashion. It's well suited for showing large datasets.<br>"
                + "An ItemClickListener can be used to react to mouse click"
                + " events. Different buttons, double click, and modifier keys"
                + " can be detected.<br/>Double-click a first or last name to"
                + " toggle it's marked state.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Table.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { FeatureSet.Tables.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
