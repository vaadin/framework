package com.vaadin.demo.sampler.features.table;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableActions extends Feature {

    @Override
    public String getName() {
        return "Table, context menu";
    }

    @Override
    public Component getExample() {
        return new TableMainFeaturesExample();
    }

    @Override
    public String getDescription() {
        return "Actions can be added to each row, and are show in a"
                + " context menu when right-clicking.";

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
