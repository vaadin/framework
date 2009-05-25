package com.vaadin.demo.sampler.features.table;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableLazyLoading extends Feature {

    @Override
    public String getName() {
        return "Table, lazy loading";
    }

    @Override
    public Component getExample() {
        return new TableMainFeaturesExample();
    }

    @Override
    public String getDescription() {
        return "Table supports lazy-loading, which means that the content is"
                + " loaded from the server only when needed. This allows the "
                + " table to stay efficient even when scrolling hundreds of"
                + " thousands of rows.<br/>Try scrolling a fair amount quickly!";

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
