package com.itmill.toolkit.demo.sampler.features.table;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Table;

public class TableColumnReordering extends Feature {

    @Override
    public Component getExample() {
        return new TableMainFeaturesExample();
    }

    @Override
    public String getDescription() {
        return "Also known as a (Data)Grid, Table can be used to show data in"
                + " a tabular fashion. It's well suited for showing large datasets.<br>"
                + " The columns can be rearranged with drag&drop - a feature"
                + " which can be enabled or disabled.";
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
