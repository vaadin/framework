package com.vaadin.demo.sampler.features.table;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableColumnReordering extends Feature {

    @Override
    public String getName() {
        return "Table, column drag&drop";
    }

    @Override
    public Component getExample() {
        return new TableMainFeaturesExample();
    }

    @Override
    public String getDescription() {
        return "The columns can be rearranged with drag&drop - a feature"
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
